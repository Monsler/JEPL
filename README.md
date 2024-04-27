## About
JEPL is interpreting JVM language, that allows to create projects like build scripts, games, applications, etc. <br>
<a href="https://github.com/Monsler/JEPL/blob/main/docs/docs-en.md">Documentation 🇬🇧</a>
<br>
<a href="https://github.com/Monsler/JEPL/blob/main/docs/docs-ru.md">Documentation 🇷🇺</a>
<br>

✅ - Fully supported <br>
🟨 - Not tested <br>
🟥 - Fully broken<br>
Supported OSES:<br>
Linux: ✅ <br>
Windows: ✅ <br>
Mac OS: 🟨 <br>
Android: 🟥 

## Hello world
```json
[
  {
    "name": "function",
    "args": ["\"main\""],
    "body": [
      {
        "name": "println",
        "args": ["\"Hello, World!\""]
      }
    ]
  }
]
```

## Variables
```json
[
  {
    "name": "variable",
    "args": ["\"name\"", "\"John\""]
  },
  {
    "name": "println",
    "args": ["\"Hello, \" + name"]
  }
]
```
