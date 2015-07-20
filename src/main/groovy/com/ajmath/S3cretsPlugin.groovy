package com.ajmath;


import org.gradle.api.Project;
import org.gradle.api.Plugin;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.AWSCredentialsProviderChain;

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class S3cretsPlugin implements Plugin<Project> {
  void apply(Project project) {
    project.extensions.create("s3crets", S3cretsPluginExtension, project)
  }
}

class S3cretsPluginExtension {

  Logger logger = LoggerFactory.getLogger(S3cretsPluginExtension.class)

  Project project
  boolean override = false
  String awsProfile = null

  S3cretsPluginExtension(Project project) {
    this.project = project
  }

  def override(val) {
    this.override = val
  }

  def awsProfile(val) {
    this.awsProfile = val
  }

  def properties(String... s3paths) {
    for (s3path in s3paths) {
      def s3ObjRef = parseS3Url(s3path)
      def s3Client = new AmazonS3Client(getCredProvider())

      def s3Object = null
      try {
        s3Object = s3Client.getObject(s3ObjRef.bucket, s3ObjRef.key)
      } catch (Exception e) {
        logger.error("Unable to fetch file from s3. S3cret properties will not "
          + "be set. Run with debug to get detailed exception")
        logger.debug("Error pulling s3crets from s3", e)
        return
      }

      def props = new Properties()
      props.load(s3Object.getObjectContent())

      props.each { key, val ->
        if (this.override || propertyNotDefined(key)) {
          this.project.set(key, val)
        }
      }
    }
  }

  def getCredProvider() {
    return new AWSCredentialsProviderChain(
      new ProfileCredentialsProvider(this.awsProfile),
      new DefaultAWSCredentialsProviderChain())
  }

  def propertyNotDefined(String property) {
    try {
      return this.project.getProperty(property) == "" ||
        this.project.getProperty(property) == null
    } catch(MissingPropertyException e) {
      return true
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
