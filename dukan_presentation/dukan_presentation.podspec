Pod::Spec.new do |spec|
    spec.name                     = 'dukan_presentation'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/TheChance101/MENA-mobile'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'DukanPresentation — internal KMP maps module for Dukan. Contains iOS-compatible map composables and shared location logic used across mobile modules.'
    spec.vendored_frameworks      = 'build/cocoapods/framework/DukanPresentation.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target    = '15.4'
                
                
    if !Dir.exist?('build/cocoapods/framework/DukanPresentation.framework') || Dir.empty?('build/cocoapods/framework/DukanPresentation.framework')
        raise "

        Kotlin framework 'DukanPresentation' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :dukan_presentation:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':dukan_presentation',
        'PRODUCT_MODULE_NAME' => 'DukanPresentation',
    }
                
    spec.script_phases = [
        {
            :name => 'Build dukan_presentation',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['build/compose/cocoapods/compose-resources']
end