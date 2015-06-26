# s3crets-gradle

Download and apply properties to a Gradle project from properties files stored in an [Amazon S3 Bucket](http://aws.amazon.com/s3/)

## Usage

AWS credentials must be specified in some manner in the default [AWS Credentials Chain](http://docs.aws.amazon.com/AWSSdkDocsJava/latest//DeveloperGuide/credentials.html#credentials-default)

```
buildscript {
    repositories {
        jcenter()
    }
}

apply plugin: 's3crets-gradle'

s3crets-gradle {
  s3Paths "s3://secrets.my.bucket/path/to/secrets.properties"
}

```

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

## History

* 2015-06-26 v0.1.0: Initial Version

## License

[WTFPL](http://www.wtfpl.net/)
