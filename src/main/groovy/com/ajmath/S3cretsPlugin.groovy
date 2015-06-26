package com.ajmath;


import org.gradle.api.Project;
import org.gradle.api.Plugin;

import com.amazonaws.services.s3.AmazonS3Client;

class S3cretsPlugin implements Plugin<Project> {
  void apply(Project project) {
    project.extensions.create("s3crets", S3cretsPluginExtension, project)
  }
}

class S3cretsPluginExtension {

  Project project
  boolean override = false

  S3cretsPluginExtension(Project project) {
    this.project = project
  }

  def override(val) {
    this.override = val
  }

  def properties(String... s3paths) {
    for (s3path in s3paths) {
      def s3ObjRef = parseS3Url(s3path)
      def s3Client = new AmazonS3Client()
      def s3Object = s3Client.getObject(s3ObjRef.bucket, s3ObjRef.key)

      def props = new Properties()
      props.load(s3Object.getObjectContent())

      props.each { key, val ->
        if (this.override || this.project.get(key) == null || this.project.get(key) == "") {
          this.project.set(key, val)
        }
      }
    }
  }

  S3ObjRef parseS3Url(String url) {
    if(!url.startsWith("s3://")) {
      throwInvalidS3PathException(url)
    }

    url = url.replace("s3://", "")

    def urlSplit = url.split("/")
    if(urlSplit.length < 2) {
      throwInvalidS3PathException(url)
    }

    def bucket = urlSplit[0]
    def key = urlSplit[1..-1].join("/")

    return new S3ObjRef(bucket: bucket, key: key)
  }

  def throwInvalidS3PathException(String url) {
    throw new IllegalArgumentException("${url} is not a valid s3 url. Please "
      + " pass in a valid s3 url in the form s3://<bucket_name>/<key>")
  }
}

class S3ObjRef {
     String bucket
     String key
}
