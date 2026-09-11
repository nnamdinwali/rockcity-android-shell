# Rockcity Android Shell

Thin Android WebView wrapper around the live Rockcity website.

- **Package:** `com.rockcity.app`
- **App name:** Rockcity
- **Live URL loaded:** https://nnamdinwali.github.io/rockcity/

Website updates appear in the app automatically. Only package/icon/permission changes need a new Play upload.

## Build

GitHub Actions builds signed APK + AAB when secrets are configured:
`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
