## Homework Demo

### How to run

	./gradlew installDebug

### Used Library

1. com.android.support:recyclerview-v7:23.1.0 

		This is a view for efficiently displaying large data sets by providing a limited window of data items.

2. com.google.code.gson:gson:2.

		This library for deserialize json data

3. com.jakewharton:butterknife:7.0.1

        Field and method binding for Android views which uses annotation processing to generate boilerplate code for you.

4. com.github.bumptech.glide:glide:3.6.1

        A library for loading images from network or disk and display to ImageView

5. rxjava and rxandroid

        A library for composing asynchronous and event-based programs using observable sequences

### About this project

#### Packages

1. activity

        MainActivity.class in this package.

2. fragment

        HotFragment.class in this package. HomeFragment is the item of ViewPager in MainActivity.

3. model

        Item.class and Items.class in this package. JSON data can deserialize to Item.class ArrayList.

4. widget

        There are three views for this project, AutoLoadRecyclerView provide a Listener to handle loadmore order, DynamicHeightImageView.java for display image use provided ratio, DividerItemDecoration.java is a decoration for RecyclerView to diplay divier line.

5. util

        GsonHelper.class provide a singleton gson object;
