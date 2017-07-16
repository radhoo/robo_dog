/*
    File:       ROBOBoard.cpp
    Version:    0.1.0
    Date:       Feb. 21, 2013
	License:	GPL v2
    
	atmega128 board code
    
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

#include <avr/io.h>
#include "timeout.h"
#include <util/delay.h>
#include "led.h"
#include "readdigipin.h"
#include "lcd/3w_hd44780_8bit.h"
#include "uart/uart.h"
#include "hbridge/dualhbridge.h"

#include <stdlib.h> //for abs

#include <string.h> //stprintf

#define ID_SENSOR_SHARP 5
#define ID_SENSOR_FRONTLEFT 4
#define ID_SENSOR_FRONTRIGHT 3
#define ID_SENSOR_LEFT 2
#define ID_SENSOR_RIGHT 1
#define ID_SENSOR_BACK 0

#define MAX_BUFFER 2

// atmega128 has two leds connected to PA0 and PA1
LED led_internal, led_lights;


//HD44780 lcd;
HD44780_3W_8BIT lcd;
UART	uart0;			// bluetooth module
DualHBridge hbridge;
ReadDigiPin pinBTConnected, pinPIR;
volatile char c;

char szMsg[255]="";

//USART0 RX interrupt this code is executed when we receive a character
ISR(USART_RX_vect){
	
	c = UDR0; //Read the value out of the UART buffer
	
	//uart0.Send(c);
	if (c == '8') {
		hbridge.Fwd();
		_delay_ms(200);
		hbridge.Stop(); c= 0;
	} else
	if (c == '2') {
		hbridge.Bck();
		_delay_ms(200);
		hbridge.Stop();c= 0;
	} else
	if (c == '4') {
		hbridge.TurnLeft();
		_delay_ms(205); // 45 degrees
		hbridge.Stop();c= 0;
	} else
	if (c == '6') {
		hbridge.TurnRight();
		_delay_ms(205); // 45 degrees
		hbridge.Stop();c= 0;
	} else 
	if (c == '7') {
		led_lights.Set(1);
	} else
	if (c == '9') {
		led_lights.Set(0);
	} else
	if (c== 13) szMsg[0] = 0;
	else
	{
		char s[2] = {0}; s[0] = c; s[1] = 0;
		strcat(szMsg, s);
	}
}

void pdelay_ms(int p) {
	for (int i=0;i<p;i++)
		_delay_ms(1);
}

void pdelay_us(int p) {
	for (int i=0;i<p;i++)
	_delay_us(1);
}

// Reads the ADC port specified by i
uint16_t adcread(uint8_t i)
{
	ADMUX = i;
	ADCSRA = (1<<ADEN) | (1<<ADIF) | (1<<ADSC)|7;
	loop_until_bit_is_set(ADCSRA, ADIF);
	uint16_t v = ADCL;
	v|= (ADCH<<8);
	return v;
}


bool GreaterTol(int a,int b, int tol) {
	return (a>b+tol);
}

float geoavg(float a, float b) {
	return sqrt(a*b);
}

void ExecuteCmd(int cmd, int per) {
	switch (cmd) {
		case 0: _delay_ms(100); break;
		case 1: {
			hbridge.TurnLeft();
			pdelay_ms(4*205);
			hbridge.Stop();
			_delay_ms(50); //noise down
		} break;
		case 2: {
			hbridge.TurnLeft();
			pdelay_ms(2*205); //90*?
			hbridge.Stop();
			_delay_ms(50);//noise down
		} break;
		case 3: {
			hbridge.TurnRight();
			pdelay_ms(2*205); //90*?
			hbridge.Stop();
			_delay_ms(50);//noise down*/
		} break;
		case 4: {
			hbridge.TurnLeft();
			//_delay_ms(100); //45*
			_delay_ms(90); //45*
			hbridge.Stop();
			hbridge.Fwd();
			
			//pdelay_ms(70);
			int time = 100+(100-per)*(100-per) / 20; //100min 600max
			pdelay_ms(time);
			hbridge.Stop();
			_delay_ms(30);
		} break;
		case 5: {
			hbridge.TurnRight();
			//_delay_ms(100); //45*
			_delay_ms(90); //45*
			hbridge.Stop();
			hbridge.Fwd();
			//pdelay_ms(70);
			int time = 100+(100-per)*(100-per) / 20; //100mmin 600max
			pdelay_ms(time);
			hbridge.Stop();
			_delay_ms(30);
		} break;
		/*case 6: {
			hbridge.Fwd();
			pdelay_ms(70);
			hbridge.Stop();
			_delay_ms(50);
		} break;*/
	}
}

void GetCmd(unsigned int sensors[], int *ret_cmd, int *ret_per) {
	//int ret_cmd = 0;
	//int ret_per = 0;
	int tol = 50;
	// form CMD / movement scenario
	if (sensors[ID_SENSOR_BACK] == 0 && 
		sensors[ID_SENSOR_LEFT] == 0 &&
		sensors[ID_SENSOR_RIGHT] == 0 && 
		sensors[ID_SENSOR_FRONTLEFT] == 0 && 
		sensors[ID_SENSOR_FRONTRIGHT] == 0) {
		*ret_cmd = 0;
	}  else if (GreaterTol(sensors[ID_SENSOR_BACK], sensors[ID_SENSOR_LEFT], tol) &&
				GreaterTol(sensors[ID_SENSOR_BACK], sensors[ID_SENSOR_RIGHT], tol) &&
				GreaterTol(sensors[ID_SENSOR_BACK], sensors[ID_SENSOR_FRONTLEFT], tol) &&
				GreaterTol(sensors[ID_SENSOR_BACK], sensors[ID_SENSOR_FRONTRIGHT], tol)) {
		*ret_cmd = 1; //turn around 180
	} else if (GreaterTol(sensors[ID_SENSOR_LEFT], sensors[ID_SENSOR_BACK], tol) &&
				GreaterTol(sensors[ID_SENSOR_LEFT], sensors[ID_SENSOR_RIGHT], tol) &&
				GreaterTol(sensors[ID_SENSOR_LEFT], sensors[ID_SENSOR_FRONTLEFT], tol) &&
				GreaterTol(sensors[ID_SENSOR_LEFT], sensors[ID_SENSOR_FRONTRIGHT], tol)) {
		*ret_cmd = 2;// turn left 90
	} else if (GreaterTol(sensors[ID_SENSOR_RIGHT], sensors[ID_SENSOR_BACK], tol) &&
				GreaterTol(sensors[ID_SENSOR_RIGHT], sensors[ID_SENSOR_LEFT], tol) &&
				GreaterTol(sensors[ID_SENSOR_RIGHT], sensors[ID_SENSOR_FRONTLEFT], tol) &&
				GreaterTol(sensors[ID_SENSOR_RIGHT], sensors[ID_SENSOR_FRONTRIGHT], tol)) {
		*ret_cmd = 3; // turn right 90
	} else if ( sensors[ID_SENSOR_FRONTLEFT] > sensors[ID_SENSOR_FRONTRIGHT]  ) {
			// can move forward : no obstacles
			if (sensors[ID_SENSOR_SHARP]!=0 && sensors[ID_SENSOR_SHARP] > 40) {
					*ret_cmd = 4;// move a bit to front-left
					*ret_per = sensors[ID_SENSOR_FRONTLEFT] / 10;
			}
			// obstacles detected
			else 
				*ret_cmd = 2;// turn left 90			
	}  else if ( sensors[ID_SENSOR_FRONTLEFT] < sensors[ID_SENSOR_FRONTRIGHT]  ) {
			// can move forward : no obstacles
			if (sensors[ID_SENSOR_SHARP]!=0 && sensors[ID_SENSOR_SHARP] > 40) {
					*ret_cmd = 5;// move a bit to front right
					*ret_per = sensors[ID_SENSOR_FRONTRIGHT] / 10;
			}					
			// obstacles detected
			else
				*ret_cmd = 3;// turn right 90
	}

}

int main(void)
{
	led_lights.Init( &PORTB, PB5);
	for (int i=0;i<6;i++) {
		led_lights.Toggle();
		_delay_ms(100);
	}		
	// init LCD: data/rs, clk, e
	lcd.Init(&PORTD,PD5, &PORTD, PB6, &PORTD, PB7);  
	lcd.lcd_cursor_home();
	lcd.lcd_string("ROBODog (C)2013   \npocketmagic.net   ");
	// init hbridge
	hbridge.Init(&PORTB, PB4, &PORTB, PB3, &PORTB, PB2, &PORTB, PB1);
	hbridge.Fwd(); _delay_ms(10); hbridge.Stop();hbridge.Bck(); _delay_ms(10); hbridge.Stop();
	// init leds
	led_internal.Init( &PORTB, PB0); 
	// init Bluetooth
	uart0.Init(0,9600, true); //uart0: Bluetooth
	// init pir sensor
	pinBTConnected.Init(&PIND, PD4);
	//pinPIR.Init(&PINB, PB5);
	// wait a little
	_delay_ms(400);
	lcd.lcd_cursor_home();
	lcd.lcd_string("* System READY *  \n****************");
	_delay_ms(400);
	lcd.lcd_clrscr();
	lcd.lcd_cursor_home();
	
	int i = 0 , cmd = 0;

	unsigned int adc_sensor[6];
	unsigned int buf_cmd[MAX_BUFFER],buf_per[MAX_BUFFER], ind = 0;
	
	while(1)
	{
		led_internal.Toggle();
		//led2.Toggle();
		i++;	
		// read sensors ADC
		for (int q=0;q<6;q++) {
			adc_sensor[q] = adcread(q);
			// ignore small values
			if (adc_sensor[q] < 5 && q!= ID_SENSOR_SHARP) adc_sensor[q] = 0;
		}			
		// convert sharp sensor results to cm;
		int sharp = adc_sensor[ID_SENSOR_SHARP];
		if (sharp <= 20) sharp = 30;
		adc_sensor[ID_SENSOR_SHARP] = 4800/(sharp - 20);
		
		
		// BLUETOOTH CONNECTION 
		if (pinBTConnected.Get()) {
			lcd.lcd_cursor_home();
			lcd.lcd_string_format("Bluetooth Link    \nFront:%dcm            ",adc_sensor[ID_SENSOR_SHARP]);
			
			uart0.Send("  [F-L:%d S:<%d> F-R:%d L:%d B:%d R:%d    ] ",
			adc_sensor[ID_SENSOR_FRONTLEFT], adc_sensor[ID_SENSOR_SHARP], adc_sensor[ID_SENSOR_FRONTRIGHT],
			adc_sensor[ID_SENSOR_LEFT], adc_sensor[ID_SENSOR_BACK], adc_sensor[ID_SENSOR_RIGHT]);
			
		} 
		// ULTRASONIC BEACON
		else {
			lcd.lcd_cursor_home(); lcd.lcd_string_format("%3d %3d %3d   \n%3d %3d %3d BT:%d  ",
			adc_sensor[ID_SENSOR_FRONTLEFT], adc_sensor[ID_SENSOR_SHARP], adc_sensor[ID_SENSOR_FRONTRIGHT],
			adc_sensor[ID_SENSOR_LEFT], adc_sensor[ID_SENSOR_BACK], adc_sensor[ID_SENSOR_RIGHT],
			pinBTConnected.Get());
		
			int cmd = 0, per = 0;
			
			GetCmd(adc_sensor, &cmd, &per);
		
			// shift buffer
			for (int q=MAX_BUFFER -1 ; q > 0;q --) {
				buf_cmd[q] = buf_cmd[q-1];
				buf_per[q] = buf_per[q-1];
			}				
			// add to buffer
			buf_cmd[0] = cmd;
			buf_per[0] = per;
			//ind++;
			//ind
			//evaluate buffer
			int docmd = 0, doper = 0;
			for (int q=1;q<7;q++) {
				int score = 0;
				for (int k=0;k<MAX_BUFFER;k++)
					if (buf_cmd[k] == q) score++;
				if (score ==MAX_BUFFER ) {
					docmd = q;
					doper = buf_per[MAX_BUFFER - 1]; // last percentage
					break;
				}			
			}		
			if (docmd != 0) {
				ExecuteCmd(docmd, doper);
				_delay_ms(30);
			}			
			else
				_delay_ms(100);
		
		}
	}		
	//http://www.pocketmagic.net/?p=3665
	//Android : http://www.pocketmagic.net/?p=1398
}