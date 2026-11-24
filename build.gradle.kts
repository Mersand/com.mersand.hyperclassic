plugins {
    id("com.android.application") version "9.1.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
}

android {
    namespace = "io.github.mersand.hyperclassic"
    compileSdk = 37
    defaultConfig {
        applicationId = "io.github.mersand.hyperclassic"
        minSdk = 33
        targetSdk = 36
        versionCode = 20101
        versionName = "2.1.1"
    }
    buildTypes {
        debug {
            // ★ GitHub 托管规范：debug = 全量日志；release = 静默工作
            // ★ 注意：不能加 applicationIdSuffix，否则包名变成 .debug，
            //   LSPosed 的 scope.list 与已授权作用域会全部失配。
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            // ★ v2.1.0：关掉资源压缩。
            //   AGP 9.1.0 的 optimizeReleaseResources 在本项目上会「假成功」——
            //   任务报 SUCCESS，但 optimized_processed_res/release/optimizeReleaseResources/
            //   目录里只有 output-metadata.json，没有任何 .ap_ 产物。
            //   结果：打包时拿不到资源 → APK 里没有 resources.arsc /
            //   AndroidManifest.xml / 任何 res 文件 → 装上直接失败。
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
    packaging {
        resources {
            merges += "META-INF/xposed/**"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            keepDebugSymbols += "**/*.so"
        }
    }
}

// ★ v2.1.0：AGP 9.1.0 的 optimizeReleaseResources 在本项目产不出资源
// （任务报 SUCCESS 但目录里只有 output-metadata.json），
// 同时 packageRelease 又强依赖它的输出，导致 release APK 里没有
// resources.arsc / AndroidManifest.xml / res 文件，装上必然失败。
//
// 但把它 disable 同样不行：packageRelease 会拿到空资源。
//
// 因此这里不做任务级干预，改为在 gradle.properties 里关掉 AGP 的
// 新资源优化管线（见 android.enableResourceOptimizations=false）。
// 保留这一段注释，方便以后 AGP 修复后回看。

dependencies {
    compileOnly("io.github.libxposed:api:102.0.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui:1.12.0")
    implementation("androidx.compose.ui:ui-graphics:1.12.0")
    implementation("androidx.compose.foundation:foundation:1.12.0")
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-preference-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-icons-android:0.9.4")
}

configurations.all {
    exclude(group = "androidx.lifecycle", module = "lifecycle-common-java8")
}