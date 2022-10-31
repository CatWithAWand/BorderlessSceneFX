<h2 align="center">BorderlessSceneFX</h2>
<p align="center">
	<a href="https://github.com/CatWithAWand/BorderlessSceneFX/releases"><img src="https://img.shields.io/github/release/CatWithAWand/BorderlessSceneFX?style=flat" alt="BorderlessSceneFX Release" /></a>
  <br>
  <strong>An undecorated JavaFX scene with move, resize, minimize, maximize, close, and Aero Snap/Quarter Tiling.</strong>
</p>


---

## About

BorderlessSceneFX is a [JavaFX](https://openjfx.io/) scene that allows you to create a borderless window with move,
resize, minimize, maximize, close, and Aero Snap/Quarter Tiling.
<br>
Both the scene and the aero snap window are customizable.

### Features

- Move
- Resize
- Minimize
- Maximize
- Close
- Aero Snap/Quarter Tiling
- Styling Aero Snap window
- Styling Main window
- Extensive API
- Customizable

![demo_aero_snap](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243031/GitHub-Assets/BorderlessSceneFX/demo_aero_snap_xbase8.gif)

This project has been forked and further developed from [this](https://github.com/goxr3plus/FX-BorderlessScene/) GitHub
repository.

## Getting Started

### Gradle

Add JitPack to your repositories

```groovy
repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```

Add BorderlessSceneFX to your dependencies

```groovy
dependencies {
    implementation 'com.github.catwithawand:borderlessscenefx:1.0.0'
}
```

### Maven

Add JitPack to your repositories

``` XML
<repositories>
   <repository>
     <id>jitpack.io</id>
     <url>https://jitpack.io</url>
   </repository>
</repositories>
```

Add BorderlessSceneFX to your dependencies

``` XML
<dependency>
  <groupId>com.github.catwithawand</groupId>
  <artifactId>BorderlessSceneFX</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Example

``` JAVA
@Override
public void start(Stage primaryStage) throws Exception {
    // Our root node
    BorderPane root = new BorderPane();
    
    // Our top bar
    HBox topBar = new Hbox();
    topBar.setMinHeight(50);
    topBar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    topBar.setStyle("-fx-background-color:#303030;");
    topBar.setAlignment(Pos.CENTER_RIGHT);
    borderPane.setTop(topBar);
    
    // A close button
    Button closeButton = new Button("x");
    closeButton.setOnAction(event -> primaryStage.close());
    
    // Create the BorderlessScene scene
    BorderlessScene scene = new BorderlessScene(primaryStage, StageStyle.TRANSPARENT, root, Color.TRANSPARENT);
    
    // Make the top bar draggable, so we can move the stage
    scene.setMoveControl(topBar);
    
    // Set the scene to our stage
    primaryStage.setScene(scene);
    
    primaryStage.setTitle("BorderlessSceneFX");
    
    // Finally show the stage
    primaryStage.show();
}
```

### Advanced example

For an actual implementation see the BorderlessSceneFX [demo application]().

## Showcase

<details>
<summary><b>Resize and the resize property</b></summary>

![demo_resizable](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243044/GitHub-Assets/BorderlessSceneFX/demo_resizable_ny1fqb.gif)

</details>

<details>
<summary><b>Vertical resize snap and preventing mouse over taskbar</b></summary>

![demo_vertical_and_taskbar](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243041/GitHub-Assets/BorderlessSceneFX/demo_vertical_and_taskbar_zcdvog.gif)

</details>

<details>
<summary><b>White Noise style</b></summary>

![demo_white_noise](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243029/GitHub-Assets/BorderlessSceneFX/demo_white_noise_bewgej.png)

</details>

<details>
<summary><b>Glass Glare style</b></summary>

![demo_glass_glare](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243029/GitHub-Assets/BorderlessSceneFX/demo_glass_glare_h8nmpn.png)

</details>

<details>
<summary><b>Glass Glare style</b></summary>

![demo_glass_transparent](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243029/GitHub-Assets/BorderlessSceneFX/demo_glass_transparent_y27eic.png)

</details>

<details>
<summary><b>Gnome style</b></summary>

![demo_gnome](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243029/GitHub-Assets/BorderlessSceneFX/demo_gnome_jlb3im.png)

</details>

<details>
<summary><b>Minimalistic style (Default)</b></summary>

![demo_minimalistic](https://res.cloudinary.com/dq6zv8koj/image/upload/v1667243029/GitHub-Assets/BorderlessSceneFX/demo_minimalistic_qixuko.png)

</details>

## License

### [MIT License](https://github.com/CatWithAWand/BorderlessSceneFX/blob/main/LICENSE)