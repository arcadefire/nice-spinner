# Nice Spinner [![](https://jitpack.io/v/arcadefire/nice-spinner.svg)](https://jitpack.io/#arcadefire/nice-spinner) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Nice%20Spinner-blue.svg?style=flat)](https://android-arsenal.com/details/1/2225)

NiceSpinner is a re-implementation of the default Android's spinner, with a nice arrow animation and a different way to display its content.

It follows the material design guidelines, and it is compatible starting from Api 14.

![alt tag](nice-spinner.gif)

### Usage

The usage is pretty straightforward. Add the tag into the XML layout:
```xml
 <org.angmarch.views.NiceSpinner
   android:id="@+id/nice_spinner"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:layout_margin="16dp"/>
```
* Note: change `layout_width` to at least the width of the largest item on the list to prevent resizing

 Then use this snippet to populate it with contents:
```java
 NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
 List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
 niceSpinner.attachDataSource(dataset);
```

#### Listeners
For listening to the item selection actions, you can just use the following snippet:
```java
spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
        // This example uses String, but your type can be any
        String item = parent.getItemAtPosition(position);
        ...
    }
});
```

#### Attributes
You can add attributes to customize the view. Available attributes:

| name                      | type      | info                                                   |
|------------------------   |-----------|--------------------------------------------------------|
| arrowTint                 | color     | sets the color on the drop-down arrow                  |
| hideArrow                 | boolean   | set whether show or hide the drop-down arrow           |
| arrowDrawable             | reference | set the drawable of the drop-down arrow                |
| textTint                  | color     | set the text color                                     |
| dropDownListPaddingBottom | dimension | set the bottom padding of the drop-down list           |
| backgroundSelector        | integer   | set the background selector for the drop-down list rows |
| popupTextAlignment        | enum      | set the horizontal alignment of the default popup text |
| entries                   | reference | set the data source from an array of strings |

How to include
---

With gradle: edit your `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.arcadefire:nice-spinner:1.4.4'
}
```

Or declare it into your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.arcadefire</groupId>
    <artifactId>nice-spinner</artifactId>
    <version>1.4.4</version>
</dependency>
```

License
-------
    Copyright (C) 2015 Angelo Marchesin.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
