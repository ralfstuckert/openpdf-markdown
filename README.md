# OpenPDF Markdown
This project lets you use simple markdown to create styled text with [OpenPDF](https://github.com/LibrePDF/OpenPDF).
Given the following markup:

```
# Tempor Invidunt
Lorem ipsum dolor sit amet, **consetetur sadipscing** elitr, sed diam _nonumy eirmod tempor invidunt_ ut labore et dolore magna ~~aliquyam~~ erat, `sed diam voluptua`. [Nam liber tempor](https://github.com/ralfstuckert/openpdf-markdown/wiki/Getting-Started)

| **Stet** | **Sanctus** | **Nam liber** |
|:---:|-----------|-------|
| Clita   | `Stet clita` kasd gubergren. | Dolore magna |
| Gubergren | Sea _takimata_ sanctus est Lorem ||

- Stet
- Clita
    1. Sanctus
    1. Lorem
    1. Dolor    
>At vero _eos et accusam et justo_ 
>duo dolores et ea rebum. 

Advancus curum adep.
```

...results in the following [PDF](https://github.com/ralfstuckert/openpdf-markdown/blob/main/src/test/resources/com/github/ralfstuckert/openpdf/markdown/example.pdf).

<kbd><img alt="example" src="https://raw.githubusercontent.com/wiki/ralfstuckert/openpdf-markdown/images/example.png" width="800px" /></kbd>

The [code](https://github.com/ralfstuckert/openpdf-markdown/blob/main/src/test/kotlin/com/github/ralfstuckert/openpdf/markdown/Example.kt) to create that is quite simple:

```kotlin
val markdown = """
# Tempor Invidunt
...
""".trimIndent()

val element = OpenPdfMarkdownGenerator().generate(markdown)

with(FileOutputStream(filename)) {
    val document: Document = Document()
    PdfWriter.getInstance(document, this)
    document.open()
    document.add(element)
    document.close()
}
```

Just head over to the [Getting Started](https://github.com/ralfstuckert/openpdf-markdown/wiki/Getting-Started) page for examples,
and information on to customize the default rendering of the markdown or even
replace the renderer code at all.

```xml
<dependency>
  <groupId>com.github.ralfstuckert</groupId>
  <artifactId>openpdf-markdown</artifactId>
  <version>0.1.0</version>
</dependency>
```

```gradle
implementation("com.github.ralfstuckert:openpdf-markdown:0.1.0")
```
