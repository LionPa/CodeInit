
# CodeInit ✨

**Code initialization framework for PaperMC plugins**  
*(Early development stage)*  

## 📌 Overview
CodeInit simplifies method execution management in Paper plugins by providing annotation-based triggers for:
- Plugin startup/shutdown events
- Manual testing via commands
- Priority-based execution ordering

## 🛠️ Setup for Developers

### 1. Configuration File
Create `.codeinit` file in your plugin's `resources` folder.

### 2. Plugin Registration
In your main class' `onEnable()` method:
```java
CodeInit.addPlugin("your.plugin.package", this);
```

## 🚀 Usage Guide

### Basic Annotations
```java
@RunOn // Runs during plugin startup (default)
@RunOn(on = RunOnType.STOP) // Runs during plugin shutdown
@RunOn(on = RunOnType.TEST) // Available via /test command
```

### Execution Priority System
Control method execution order with priorities:
```java
@RunOn(priority = 0)    // First to execute
@RunOn(priority = 1)    // Runs after priority 0
@RunOn(priority = -2)   // Runs before final methods
@RunOn(priority = -1)   // Last to execute (default)
```

### 🎮 /test Command Features
Manually trigger test methods with automatic argument handling:
```
/test ClassName:methodName [args]
```
*Automatically injects Player argument when required*

## 📝 Example Usage
```java
public class TestClass {
    
    @RunOn(priority = 0)
    public void startupTask() {
        // Runs first during plugin startup
    }
    
    @RunOn(on = RunOnType.TEST)
    public void testMethod(Player player) {
        player.sendMessage("Test successful!");
    }
}

```
