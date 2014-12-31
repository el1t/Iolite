# How to contribute

Simply fork and clone this repo, and [download][http://developer.android.com/sdk/index.html] Android Studio to start coding!

When you have made a satisfactory change (and tested it!), [submit][https://github.com/el1t/Iolite/compare/] a pull request.

## Style guidelines

* Remove trailing whitespace
* Tab indentation
* Do not add a newline before `{`, unless declaring a class
* Use an empty line between methods
* Use camelCasing in Java files, including naming
* Use underscores in xml and res files, including naming
* Use spaces between operands
* Prefix class variables with `m`
* Tag overridden methods with `@Override`
* Keep overriden methods at the top of the file
* Avoid/break lines longer than 100 characters
* Order layout properties as follows: `id`, `layout_width`, `layout_height`, other `layout_` properties, other properties
* Write a [good commit message!][https://github.com/erlang/otp/wiki/Writing-good-commit-messages]

Anything not mentioned here? Just take a look at the source code.

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

Based on thoughtbot's [contributing.md][https://github.com/thoughtbot/factory_girl_rails/blob/master/CONTRIBUTING.md] and [style guide][https://github.com/thoughtbot/guides/tree/master/style].