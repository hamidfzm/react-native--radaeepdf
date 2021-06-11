# react-native-radaeepdf

React Native RadaeePDF for Android, iOS, &amp; Windows

## Features
- [x] Cross Platform
- [x] Typescript

## Installation

### npm
```sh
npm install --save react-native-radaeepdf
```

### yarn
```sh
yarn add react-native-radaeepdf
```

## Proguard
If you use Proguard you will need to add these lines to `android/app/proguard-rules.pro`:

```
-keep class com.radaee.** { *; }
-keepclasseswithmembernames class com.radaee.** { *; }
```

## Implementation status

| Implementation 	| Android 	| iOS 	| Windows 	| Web 	|
|----------------	|---------	|-----	|---------	|-----	|
| Module         	| ✅       	| ✅   	| ❌       	| ❌   	|
| View           	| ✅       	| ❌   	| ❌       	| ❌   	|

## Usage
There are 2 ways to use this library:

1. Using library module. In this way you don't need to implement anything.
    ```js
    import Radaeepdf from "react-native-radaeepdf";

    // ...
    Radaeepdf.Activate(license);
    Radaeepdf.Show(pdfURI);
    ```

2. Using PDFView (In progress)

## Contributing
See the [CONTRIBUTING](CONTRIBUTING.md) file for how to help out.

## Release Notes
See the [CHANGELOG](CHANGELOG.md).

## License
This library is licensed under the [MIT Licence](LICENSE).
