package com.listhub.gradle;


import org.gradle.api.Project;
import org.gradle.api.Plugin;

import com.amazonaws.services.s3.AmazonS3Client;


class ListhubBuildPlugin implements Plugin<Project> {
  void apply(Project project) {

    // TODO: get this from dsl
    def s3paths = ["s3://secrets.us-east-1.listhub.net/gradle/artifactory.properties"]
    for (s3path in s3paths) {
      def s3ObjRef = parseS3Url(s3path)
      def s3Client = new AmazonS3Client();
      def s3Obj = s3Client.getObject(s3ObjRef.bucket, s3ObjRef.key)

      def props = new Properties()
      props.load(s3Obj.getObjectContent())

      props.each { key, val ->
        project.set(key, val)
      }
    }
  }

  S3OBj parseS3Url(String url) {
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

    return new S3Obj(bucket: bucket, key: key)
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
