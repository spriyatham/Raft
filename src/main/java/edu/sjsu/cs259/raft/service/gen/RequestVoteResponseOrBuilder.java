// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package edu.sjsu.cs259.raft.service.gen;

public interface RequestVoteResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:raft.RequestVoteResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint64 term = 1;</code>
   */
  long getTerm();

  /**
   * <code>bool voteGranted = 2;</code>
   */
  boolean getVoteGranted();
}
