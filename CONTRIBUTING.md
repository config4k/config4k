# Contributing to Config4k
Welcome and thank you for your interest in Config4k!
## How to submit changes?
1. [Open issue](https://github.com/config4k/config4k/issues) to make what you do clearer
2. Test your code if necessary. See an [example](https://github.com/config4k/config4k/blob/main/src/test/kotlin/io/github/config4k/TestExtension.kt)
3. Create pull request

## Documentation preview

For check documentation use the next steps.

Run the docker container from root folder of this repo:
```bash
docker run --rm -it -p 8000:8000 -v ${PWD}:/docs squidfunk/mkdocs-material
```

Open [http://0.0.0.0:8000/](http://0.0.0.0:8000/) in your browser.
