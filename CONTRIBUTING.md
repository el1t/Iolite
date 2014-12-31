# How to contribute

Simply fork and clone this repo, and [download][http://developer.android.com/sdk/index.html] Android Studio to start coding!

When you have made a satisfactory change (and tested it!), [submit][https://github.com/el1t/Iolite/compare/] a pull request.

## Style guidelines

Following these style guidelines will increase the chance that your pull request will be accepted!

### General
* Remove trailing whitespace
* Tab indentation
* Avoid/break lines longer than 100 characters

### Java
* Use camelCasing in Java files, including naming
* Use an empty line between methods
* Use spaces between operands
* Do not add a newline before `{`, unless declaring a class
* Prefix class variables with `m`
* Tag overridden methods with `@Override`
* Keep overriden methods at the top of the file

### Resource files
* Use underscores in xml and res files, including naming
* Order views in the order that they would appear on a device from top to bottom, left to right
* Order layout properties as follows: `id`, `layout_width`, `layout_height`, other `layout_` properties, other properties
* Write a [good commit message][https://github.com/erlang/otp/wiki/Writing-good-commit-messages]!

## Coding guidelines

* Implement `Parcelable` rather than `Serializable`
* Use fragments
* Simplify the view hierarchy in layouts whenever possible
  * Prefer `RelativeLayout` over nesting layouts (avoid nesting in most situations)
  * Use a `FrameLayout` if possible to reduce overhead
  * Remove unnecessary top-level layouts
* When calling `Log`, use a class variable as a tag, e.g. `private static final String TAG`
  * Preferred `Log.d` call style: `Log.d(TAG, "Text");`
  * Preferred `Log.e` call style: `Log.e(TAG, "Error text", new Error());`

## Recommended

* Optimize imports frequently (`Code` -> `Optimize Imports`)
* Inspect code occasionally (`Analyze` -> `Inspect Code`)

Anything not mentioned here? Just take a look at the source code.
Based on thoughtbot's [contributing.md][https://github.com/thoughtbot/factory_girl_rails/blob/master/CONTRIBUTING.md] and [style guide][https://github.com/thoughtbot/guides/tree/master/style].