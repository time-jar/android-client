
decrypt:
	sops -d .enc.secrets.properties > secrets.properties

prepare-to-publish:
	@if [ -f app/build/outputs/apk/debug/app-debug.apk ]; then \
		mv app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/debug/Time-Jar.apk; \
		rm app/build/outputs/apk/debug/output-metadata.json; \
	fi
	@cp "./Device install instructions.md" "app/build/outputs/apk/debug/Device install instructions.md"
