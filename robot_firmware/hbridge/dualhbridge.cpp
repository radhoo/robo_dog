/*
    File:       DualHBridge.cpp
    Version:    0.1.0
    Date:       Feb. 21, 2013
	License:	GPL v2
    
	DualHBridge Class code, to allow simple use of DualHBridges controlDualHBridge directly by I/O pins
    
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



#include "DualHBridge.h"

/*void DualHBridge::Init(int reg, int port, int dq) {
	m_reg = reg;
	m_port = port;
	m_dq = dq;
	
	_SFR_IO8(m_reg) |=(1<<dq);
}
void DualHBridge::Set(int st) {
	DualHBridge_state = st;
	if (st)
	_SFR_IO8(m_port) |= (1<<m_dq);
	else
	_SFR_IO8(m_port) &= ~(1<<m_dq);
}*/
void DualHBridge::Init(volatile uint8_t  *port_m1f, uint8_t  m1f,
							volatile uint8_t  *port_m1b, uint8_t  m1b,
							volatile uint8_t  *port_m2f, uint8_t  m2f,
							volatile uint8_t  *port_m2b, uint8_t  m2b
) {
	// save globals
	m_port_m1f = port_m1f;
	m_port_m1b = port_m1b;
	m_port_m2f = port_m2f;
	m_port_m2b = port_m2b;
	m_m1f = m1f;
	m_m1b = m1b;
	m_m2f = m2f;
	m_m2b = m2b;
	// set for output
	*Port2DDR(m_port_m1f) |= (1<<m_m1f);
	*Port2DDR(m_port_m1b) |= (1<<m_m1b);
	*Port2DDR(m_port_m2f) |= (1<<m_m2f);
	*Port2DDR(m_port_m2b) |= (1<<m_m2b);
}

/************************************************************************/
/* state: 0: motor off, 1: motor fwd, 2: motor back                     */
/************************************************************************/
void DualHBridge::Motor(int motor, int state) {
	if (motor == 0) {
		switch (state) {
			case 0: 
				*m_port_m1f &= ~(1<<m_m1f);
				*m_port_m1b &= ~(1<<m_m1b);
			break;
			case 1: // fwd
				*m_port_m1f |= (1<<m_m1f);
				*m_port_m1b &= ~(1<<m_m1b);
			break;
			case 2: // back
				*m_port_m1f &= ~(1<<m_m1f);
				*m_port_m1b |= (1<<m_m1b);
			break;
		}
	} else  {
		switch (state) {
			case 0:
			*m_port_m2f &= ~(1<<m_m2f);
			*m_port_m2b &= ~(1<<m_m2b);
			break;
			case 1: // fwd
			*m_port_m2f |= (1<<m_m2f);
			*m_port_m2b &= ~(1<<m_m2b);
			break;
			case 2: // back
			*m_port_m2f &= ~(1<<m_m2f);
			*m_port_m2b |= (1<<m_m2b);
			break;
		}
	}	
}

void DualHBridge::Stop() {
	Motor(0,0);
	Motor(1,0);
}	

void DualHBridge::Fwd() {
	Motor(0,1);
	Motor(1,1);
}

void DualHBridge::Bck() {
	Motor(0,2);
	Motor(1,2);
}

void DualHBridge::TurnLeft() {
	Motor(0,1);
	Motor(1,2);
}
void DualHBridge::TurnRight() {
	Motor(0,2);
	Motor(1,1);
}