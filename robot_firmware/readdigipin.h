/*
    File:       pir.h
    Version:    0.1.0
    Date:       Feb. 21, 2013
	License:	GPL v2
    
	header file for the PIR sensor code
    
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

class ReadDigiPin {	
	volatile uint8_t *m_ppin;
	uint8_t m_ledstate, m_dq;
	
	volatile uint8_t* Pin2DDR(volatile uint8_t *pin) {
		return pin + 1;
	}
	
	public:
	/************************************************************************/
	/* Set DDR for output and save globals                                  */
	/************************************************************************/
	void Init(volatile uint8_t  *pin, uint8_t  dq) ;
	/************************************************************************/
	/* return sensor status (0=no detection, 1=detection)                   */
	/************************************************************************/
	uint8_t Get();
};
