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
docker run --rm --volume="$PWD:/srv/jekyll:Z" --publish 4000:4000 -it jekyll/jekyll:4 sh
```

Inside the running container run the next commands:
```bash
cd docs
bundle install
rm -rf _site/ && bundle exec jekyll serve -H 0.0.0.0 --incremental
```

Open [http://localhost:4000/config4k](http://localhost:4000/config4k/) in your browser.
