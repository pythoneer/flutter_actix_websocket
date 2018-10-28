import 'package:flutter/material.dart';

import 'package:web_socket_channel/io.dart';
import 'package:web_socket_channel/status.dart' as status;

import 'dart:io';

var socket;
var channel;

void main() {
  channel = IOWebSocketChannel.connect('ws://echo.websocket.org');
//  channel = IOWebSocketChannel.connect('ws://192.168.188.43:8765');  //python ws server
//  channel = IOWebSocketChannel.connect('ws://192.168.188.43:8080/ws/'); //actix ws server

  print("ch: $channel");

  channel.stream.listen((message) {
    print("msg: $message");
  });

  channel.sink.add('ws test1');
  channel.sink.add('ws test2');
  channel.sink.add('ws test3');

  print("after socket");

  rawws();
}

void rawws() async {

  print("raw ws");

  socket = await WebSocket.connect('ws://echo.websocket.org');
//  socket = await WebSocket.connect('ws://192.168.188.43:8765');   //python ws server
//  socket = await WebSocket.connect('ws://192.168.188.43:8080/ws/'); //actix ws server

  print("rs: ${socket.readyState}");
  print("pr: ${socket.protocol}");

  socket.add("raw test1");
  socket.add("raw test2");
  socket.add("raw test3");

  await for (var msg in socket) {
    print("msg: $msg");
  }
}
