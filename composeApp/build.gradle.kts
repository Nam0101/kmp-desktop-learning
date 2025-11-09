import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.example.testdesktop.MainKt"

        nativeDistributions {
            // Cấu hình format cho từng platform
            targetFormats(
                TargetFormat.Exe,  // Windows
                TargetFormat.Dmg,  // macOS
                TargetFormat.Deb   // Linux
            )
            packageName = "TestDesktop"
            packageVersion = "1.0.0"
            description = "Test Desktop Application"
            vendor = "Example"
            
            windows {
                menuGroup = "TestDesktop"
                upgradeUuid = "18159995-d967-4cd2-8885-77BFA97CFA9F"
                dirChooser = true
                perUserInstall = true
                // iconFile.set(project.file("src/jvmMain/resources/icon.ico")) // Uncomment nếu có icon
            }
            
            macOS {
                bundleID = "com.example.testdesktop"
                // iconFile.set(project.file("src/jvmMain/resources/icon.icns")) // Uncomment nếu có icon
            }
            
            linux {
                packageName = "testdesktop"
                debMaintainer = "example@example.com"
                // iconFile.set(project.file("src/jvmMain/resources/icon.png")) // Uncomment nếu có icon
            }
        }
    }
}
