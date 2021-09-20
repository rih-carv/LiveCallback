# How to become a contributor and submit your own code

We'd love to accept your sample apps, issues and patches!

## Contributing A Patch

1. Submit an issue describing your proposed change to discuss it first.
2. Fork the repo, develop and test your code changes.
3. Ensure that your code adheres to the existing style.
4. Ensure that your code has an appropriate set of unit tests which all pass.
5. Submit a pull request.

## Code Style

This project uses [ktlint](https://github.com/pinterest/ktlint), provided via the
[spotless](https://github.com/diffplug/spotless) gradle plugin, and the bundled project IntelliJ
code style.

If you find that one of your pull requests does not pass the CI server check due to a code style
conflict, you can easily fix it by running: `./gradlew spotlessApply`.