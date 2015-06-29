# gradle-s3crets

Download and apply properties to a Gradle project from properties files stored in an [Amazon S3 Bucket](http://aws.amazon.com/s3/)

## Usage

AWS credentials must be specified in some manner in the default [AWS Credentials Chain](http://docs.aws.amazon.com/AWSSdkDocsJava/latest//DeveloperGuide/credentials.html#credentials-default)

Check https://plugins.gradle.org/plugin/com.ajmath.gradle-s3crets for the latest version

Gradle file:

```gradle
// Before gradle 2.1
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath(group: 'com.ajmath', name: 'gradle-s3crets', version: '1.0.0')
  }
}

// Gradle 2.1 and later
plugins {
  id "com.jfrog.bintray" version "1.1"
  id "com.ajmath.gradle-s3crets" version "1.0.0"
}

apply plugin: 'com.ajmath.gradle-s3crets'

s3crets {
  properties "s3://secrets.my.bucket/path/to/secrets.properties"
}

```

By default, the properties loaded from the S3 properties files will *not* override properties defined in the project.  To disable this behavior, set override to true:

```
s3crets {
  override true
  properties "s3://secrets.my.bucket/path/to/secrets.properties"
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
