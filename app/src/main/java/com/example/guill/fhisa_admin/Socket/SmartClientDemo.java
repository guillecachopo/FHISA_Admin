package com.example.guill.fhisa_admin.Socket;

public class SmartClientDemo {
	public static void main(String[] args) {
		SmartSocket socket = new SmartSocket("89.17.197.73", 6905, new SmartSocket.SmartSocketCallback() {
			
			@Override
			public void onNewData(SmartSocket socket, byte[] data) {
				try {
					System.out.println("New data: " + new String(data, "UTF-8"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onInitSuccess(SmartSocket socket) {
				try {
					socket.send("860935033015443, vehiculo, /n".getBytes("UTF-8"));
					System.out.println("Sent data");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFail(SmartSocket socket, Exception e) {
				e.printStackTrace();
				socket.suicide();
			}
		});
	}
}
