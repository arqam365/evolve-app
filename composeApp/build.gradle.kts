import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodGenTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods {
        version = "6.3"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ComposeApp"
            isStatic = true
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = false // This is default .
        }

        extraSpecAttributes["vendored_frameworks"] = "['../iosApp/Frameworks/YCProductSDK.framework']"
//        pod("YCProductSDK-Swift") {
//            version = "0.4.2"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.websockets)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)

            implementation("com.google.code.gson:gson:2.13.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.ring.evolve"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].java.srcDirs("src/androidMain/kotlin")
    sourceSets["main"].jniLibs.srcDir("libs")
    sourceSets["main"].resources.srcDir("libs")

    defaultConfig {
        applicationId = "org.ring.evolve"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


tasks.withType<PodGenTask>().configureEach {
    doLast {
        val podfile = podfile.get()
        var content = podfile.readText()

        // Ensure Swift version is set globally
        if (!content.contains("ENV['SWIFT_VERSION']")) {
            content = "ENV['SWIFT_VERSION'] = '5.0'\n\n" + content
        }

        // Replace post_install block to inject custom config
        content = content.replace(
            "post_install do |installer|",
            """
post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      # 1. Disable signing
      config.build_settings['EXPANDED_CODE_SIGN_IDENTITY'] = ""
      config.build_settings['CODE_SIGNING_REQUIRED'] = "NO"
      config.build_settings['CODE_SIGNING_ALLOWED'] = "NO"

      # 2. Raise deployment target if needed
      deployment_target_split = config.build_settings['IPHONEOS_DEPLOYMENT_TARGET']&.split('.')
      deployment_target_major = deployment_target_split&.first&.to_i
      deployment_target_minor = deployment_target_split&.second&.to_i

      if deployment_target_major && deployment_target_minor
        if deployment_target_major < 11 || (deployment_target_major == 11 && deployment_target_minor < 0)
          version = "#{11}.#{0}"
          puts "Deployment target for #{target} #{config} has been raised to #{version}. See KT-57741 for more details"
          config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = version
        end
      end

      # 3. Fix for Swift files in AWS Pods
      config.build_settings['SWIFT_VERSION'] = '5.0'

      # 4. Exclude arm64 for simulator
      # config.build_settings["EXCLUDED_ARCHS[sdk=iphonesimulator*]"] = "arm64"
      # config.build_settings['ONLY_ACTIVE_ARCH'] = 'YES'
      # config.build_settings['VALID_ARCHS'] = 'x86_64'
    end
  end

""".trimIndent()
        )

        podfile.writeText(content)
    }
}