/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Created by IntelliJ IDEA.
 * User: mjr
 * Date: 05.12.11
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */

package {
	import flash.display.Sprite;
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.external.ExternalInterface;
	import flash.net.Socket;
	import flash.system.Security;
	import flash.utils.ByteArray;
	import flash.utils.Timer;

	public class FlashSocket extends Sprite {
		private var socket:Socket;
		private var host:String;
		private var port:int;

		public function FlashSocket() {
			if (ExternalInterface.available) {
				ExternalInterface.addCallback("connect", connect);
				ExternalInterface.addCallback("send", sendMessage);
			}
			this.host = this.loaderInfo.parameters.host;
			this.port = this.loaderInfo.parameters.port;
			if (host && port) {
				connect();
			}
		}

		private function connect():void {
			if (this.socket == null || !this.socket.connected) {
				try {
					trace("start");
					Security.loadPolicyFile("xmlsocket://" + host + ":" + port);
					this.socket = new Socket(host, port)
					this.socket.addEventListener(Event.CONNECT, onSocketAction);
					this.socket.addEventListener(Event.CLOSE, onSocketAction);
					this.socket.addEventListener(Event.CLOSE, onSocketAction);
					this.socket.addEventListener(ProgressEvent.SOCKET_DATA, onMessageReceived);
					this.socket.addEventListener(IOErrorEvent.IO_ERROR, onError);
					this.socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onError);

					var timer:Timer = new Timer(60000);
					timer.addEventListener("timer", ping);
					timer.start();
				} catch(e:Error) {
					if (ExternalInterface.available) {
						ExternalInterface.call("onError", e.getStackTrace());
					}
				}
			}
		}

		private function onSocketAction(event:Event):void {
			if (ExternalInterface.available) {
				if (event.type == Event.CONNECT) {
					ExternalInterface.call("onConnected");
				} else if (event.type == Event.CLOSE) {
					ExternalInterface.call("onDisconnected");
					socket.close();
				}
			}
		}

		private function onMessageReceived(event:ProgressEvent):void {
			var array:* = new ByteArray();
			this.socket.readBytes(array);
			var msg:String = array.toString();
			if (msg == "pong\n") {
				trace("pong");
				return;
			}
			for each (var json:String in msg.split("\n")) {
				if (json.length != 0) {
					ExternalInterface.call("onMessage", json);
				}
			}
		}

		private function sendMessage(json:String):void {
			trace(json);
			json = json + "\n";
			var array:* = new ByteArray();
			array.writeUTFBytes(json);
			this.socket.writeBytes(array);
			this.socket.flush();
		}

		private function onError(event:ErrorEvent):void {
			if (ExternalInterface.available) {
				ExternalInterface.call("onError", event.toString());
			}
		}

		public function ping(event:TimerEvent):void {
			if (socket.connected) {
				sendMessage("ping");
			} else {
				socket.connect(host, port);
			}
		}
	}
}


