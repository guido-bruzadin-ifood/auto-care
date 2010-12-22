package br.com.tribotech.autocare.obd2;

public enum Protocol {

	/**
	 *  Consulte: http://en.wikipedia.org/wiki/On-board_diagnostics 
	 *  para mais informações.
	 * 
	 */
	
	/***
	 *  Representação das pinagens do OBD2:
	 *   
	 *     1             8  
	 *   -------------------
	 *  \  o o o o o o o o  /   
	 *   \ o o o o o o o o /
	 *     ---------------
	 *     9             16
	 *     
	 *  All OBDII pinouts use the same connector but different pins are utilized with the exception of pin 4 (battery ground) and pin 16 (battery positive).
	 */

	/**** ISO 15765 CAN  (250 kBit/s or 500 kBit/s). The CAN protocol is a popular standard outside of the US automotive industry and is making significant in-roads into the OBD-II market share. By 2008, all vehicles sold in the US will be required to implement CAN, thus eliminating the ambiguity of the existing five signalling protocols.
	 *
	 *		pin 6: CAN High
	 *		pin 14: CAN Low
	 *	
	 *	Pinos: 5,6,14 e 16 ativos
	 */
	CAN( 2^4 | 2^5 | 2^13 | 2^15), 
	
	/**** ISO 9141-2. This protocol has an asynchronous serial data rate of 10.4 kBaud. It is somewhat similar to RS-232, but that the signal levels are different, and that communications happens on a single, bidirectional line without extra handshake signals. ISO 9141-2 is primarily used in Chrysler, European, and Asian vehicles.
	 * 	
	 * 		pin 7: K-line
	 * 		pin 15: L-line (optional)
	 * 		UART signaling (though not RS-232 voltage levels)
	 * 		K-line idles high	
	 * 		High voltage is Vbatt
	 * 		Message length is restricted to 12 bytes, including CRC
	 * 
	 *	Pinos: 5,7,15 e 16 ativos - Nesse Protocolo o pino 15 é opicional e pode não existir no veículo
	 */

	/**** ISO 14230 KWP2000 (Keyword Protocol 2000)
	 *
	 *		pin 7: K-line
	 *		pin 15: L-line (optional)
	 *		Physical layer identical to ISO 9141-2
	 *		Data rate 1.2 to 10.4 kBaud
	 *		Message may contain up to 255 bytes in the data field	
	 *	
	 *	Pinos: 5,7,15 e 16 ativos - Nesse Protocolo o pino 15 é opicional e pode não existir no veículo
	 */
	ISO_KWP( 2^4 | 2^6 | 2^15),

	/**** SAE J1850 VPW (variable pulse width - 10.4/41.6 kB/sec, standard of General Motors)
	 *
	 *		pin 2: Bus+
	 *		Bus idles low
	 *		High voltage is +7 V
	 *		Decision point is +3.5 V
	 *		Message length is restricted to 12 bytes, including CRC
	 *		Employs CSMA/NDA
	 *	
	 *	Pinos: 2,5 e 16 ativos
	 */
	VPW( 2^1 | 2^4 | 2^15), 
	
	/**** SAE J1850 PWM (pulse-width modulation - 41.6 kB/sec, standard of the Ford Motor Company)
	 *		pin 2: Bus+
	 *		pin 10: Bus–
	 *		High voltage is +5 V
	 *		Message length is restricted to 12 bytes, including CRC
	 *		Employs a multi-master arbitration scheme called 'Carrier Sense Multiple Access with Non-Destructive Arbitration' (CSMA/NDA)
	 *
	 * 	Pinos: 2,5,10 e 16 ativos
	 */
	PWM( 2^1 | 2^4 | 2^9 | 2^15); 

	private int codigo;

	private Protocol(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo(){
		return this.codigo;
	}
	
	public String getName(){
		switch(this){
			case CAN: return "CAN";
			case ISO_KWP: return "ISO or KWP";
			case VPW: return "VPW";
			case PWM: return "PWM";
			
			default:
				return "OBD2 - protocolo indefinido";
		}
	}
	
	public String getPins(){
		switch(this){
			case CAN: return "Pinos Ativos: 5, 6, 14 e 16.";
			case ISO_KWP: return "Pinos Ativos: 5, 7, 15 e 16. O pino 15 é opicional";
			case VPW: return "Pinos Ativos: 2, 5 e 16.";
			case PWM: return "Pinos Ativos: 2, 5, 10 e 16.";
			
			default:
				return "OBD2 - protocolo indefinido. Sem pinos ativos.";
		}
	}
	
}
