# Nice Spinner

NiceSpinner is a re-implementation of the default Android's spinner, with a nice arrow animation and a different way to display its content.

It follows the material design guidelines, and it is compatible starting from Api 14.

![alt tag](nice-spinner.gif)

### Usage
The usage is pretty straightforward. Add the tag into the XML layout, then use this snippet to populate with contents:

```java
 NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
 List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
 niceSpinner.attachDataSource(dataset);
```

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
    compile 'com.github.arcadefire:nice-spinner:1.1'
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
    <version>1.1</version>
</dependency>
```

License
-------
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.