# android-client

To decrypt variables/secrets, run:

```sh
export SOPS_AGE_KEY=...

make decrypt
```

Builds are [here](app/build/outputs/apk/debug/).

Run the following to prepare it for publishing:

```sh
make prepare-to-publish
```

Some images were gathered from [Flaticon](https://www.flaticon.com).

## Docs

[DetectedActivity](https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity)
