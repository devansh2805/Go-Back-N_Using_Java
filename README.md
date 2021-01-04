# Implementation of the Go-Back-N ARQ Algorithm
![GitHub repo size](https://img.shields.io/github/repo-size/devansh2805/Go-Back-N_Using_Java?style=plastic)
[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)](https://github.com/devansh2805) [![Documentation](https://img.shields.io/badge/Documentation%3F-yes-green.svg)](https://github.com/devansh2805)  
[![forthebadge made-with-java](http://ForTheBadge.com/images/badges/made-with-java.svg)](https://www.java.com/en/)
  
*Go Back N Algorithm is used in the Data Link Layer of the OSI Model of Networking. Data Link Layer holds responsibility of Error Control and Flow Control in the Network. Go Back N ARQ is used in Flow Control.*

## Important Pointers regarding Go-Back-N ARQ
1. Frames from sender are numbered sequentially.
2. If m bit sequence number is used, the numbers range from 0 -> 2<sup>m</sup>-1.
3. Without receiving acknowledgement, Sender can send up to 2<sup>m</sup>-1 Frames together.
4. Receiver sends Acknowledgement if and only if it receives frames in order and without error.
5. Receiver sends Ack Frame with Sequence number of the next expecting Frame.
6. Sender sets timer upon transmission. If acknowledgement is not received in specified time, Retransmission is Triggered.
7. ARQ - Automatic Repeat Request is explained by the above point.

### Sender Side Algorithm    
```
	Sw = 2^m - 1
	Sf = 0
	Sn = 0
	
	while(true) {

		waitForEvent();

		if(Event is Request to Send from Network Layer) {
			if(Sn - Sf >= Sw) {
				sleep();
			}
			getData();
			MakeFrame(Sn);
			storeFrame(Sn);
			sendFrame(Sn);
			Sn = Sn + 1;
			if(timer not running) {
				startTimer();
			}
		}

		if(Event is Arrival Notification of ACK Frame) {
			receive(ACK);
			if(ACK is corrupted) {
				sleep();
			}
			if(ackNo > Sf && ackNo <= Sn ) {
				while(Sf <= ackNo) {
					purgeFrame(Sf);
					Sf = Sf +1;
				}
				stopTimer();
			}
		}

		if(Event is TimeOut) {
			startTimer();
			temp = Sf;
			while(temp < Sn) {
				sendFrame(temp);
				temp = temp + 1;
			}
		}
	} 
```
### Receiver Side Algorithm

``` 
	Rn = 0;
	while(true) {
		waitForEvent();

		if(Event is Arrival Notification of Data Frame) {
			receive(dataFrame);
			if(dataFrame is corrupted) {
				sleep();
			}
			if(sequenceNumber of dataFrame == Rn) {
				deliverDataToNetworkLayer();
				Rn = Rn + 1;
				sendACK(Rn);
			}
		}
	} 

```
This implementation of Go-Back-N ARQ is done as a programming depiction of flow control in Data Link Layer. It does not replicate the Flow Control in real since the network layer can't be implemented, but is very close to actual implementation. Another difference is that the loss of Frames is given as not sending here (Applied using Randomness) since on localhost corruption of frame is least likely. 