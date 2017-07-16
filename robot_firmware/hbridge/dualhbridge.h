/*
    File:       hbridge.h
    Version:    0.1.0
    Date:       Feb. 21, 2013
	License:	GPL v2
    
	Header file defining hbridge Class to allow simple use of hbridges controlhbridge directly by I/O pins
    
    ****************************************************************************
    Copyright (C) 2013 Radu Motisan  <radu.motisan@gmail.com>
	
	http://www.pocketmagic.net

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    ****************************************************************************
 */


#pragma once

// c:\Program Files\Atmel\AVR Tools\AVR Toolchain\avr\include\avr\sfr_defs.h
// #define _SFR_IO8(io_addr) ((io_addr) + __SFR_OFFSET)
// #define _SFR_IO16(io_addr) ((io_addr) + __SFR_OFFSET)

#include <avr/io.h>

class DualHBridge {	
	volatile uint8_t *m_port_m1f, *m_port_m1b, *m_port_m2f, *m_port_m2b;
	uint8_t m_m1f, m_m1b, m_m2f, m_m2b;
	
	volatile uint8_t* Port2DDR(volatile uint8_t *port) {
		return port - 1;
	}
	
	void Motor(int motor, int state) ;
	
	public:
	/************************************************************************/
	/* Set DDR for output and save globals                                  */
	/************************************************************************/
	void Init(volatile uint8_t  *port_m1f, uint8_t  m1f,
				volatile uint8_t  *port_m1b, uint8_t  m1b,
				volatile uint8_t  *port_m2f, uint8_t  m2f,
				volatile uint8_t  *port_m2b, uint8_t  m2b
			) ;
	void Stop();

	void Fwd();

	void Bck();
	
	void TurnLeft() ;
	
	void TurnRight();
};
