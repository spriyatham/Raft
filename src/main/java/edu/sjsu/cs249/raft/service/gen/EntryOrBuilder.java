// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package edu.sjsu.cs249.raft.service.gen;

public interface EntryOrBuilder extends
    // @@protoc_insertion_point(interface_extends:raft.Entry)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint64 term = 1;</code>
   */
  long getTerm();

  /**
   * <code>uint64 index = 2;</code>
   */
  long getIndex();

  /**
   * <code>string decree = 3;</code>
   */
  java.lang.String getDecree();
  /**
   * <code>string decree = 3;</code>
   */
  com.google.protobuf.ByteString
      getDecreeBytes();
}