package main

import (
	"fmt"
	"net"
	"os"
)

/**
 *	Opens a socket listening for clients.
 *
 *	@param serverHost - The server address.
 *	@param serverPort - The port to listen on.
 *	@param serverType - The protocol. TCP is standard.
 */
func ListenForClients(serverHost string, serverPort string, serverType string) {
	fmt.Println("Server Running...")

	server, err := net.Listen(serverType, serverHost+":"+serverPort)
	if err != nil {
		fmt.Println("Error listening:", err.Error())
		os.Exit(1)
	}
	defer server.Close()

	fmt.Println("Listening on " + serverHost + ":" + serverPort)

	fmt.Println("Waiting for client...")
	for {
		connection, err := server.Accept()
		if err != nil {
			fmt.Println("Error accepting: ", err.Error())
			os.Exit(1)
		}
		fmt.Println("client connected")
		go ProcessClient(connection)
	}
}

/**
 *	Processes a client.
 *
 *	@param connection - The connection to the client.
 */
func ProcessClient(connection net.Conn) {
	defer connection.Close()

	for {
		buffer := make([]byte, 1024)
		mLen, err := connection.Read(buffer)
		if err != nil {
			fmt.Println("Error reading:", err.Error())
			fmt.Println("(client shut down)")
			return
		}

		fmt.Println("Received: ", string(buffer[:mLen]))
	}
}
