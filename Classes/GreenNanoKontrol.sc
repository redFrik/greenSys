//nanoKONTROL v1 (9 sliders & 9 knobs, scene #1, factory settings)

//related: GreenNanoKontrolGUI, GreenNanoKontrol2

GreenNanoKontrol : AbstractGreenMIDIController {

	const <ctrlSymbols= #[\rew, \play, \ff, \loop, \stop, \rec];

	var <ctrl, <knobs, <sliders, <buttons1, <buttons2;

	*deviceName {^"nanoKONTROL"}

	loop {^cvs[\loop]}
	stop {^cvs[\stop]}

	gui {|position|
		^GreenNanoKontrolGUI(nanoKontrol:this).moveTo(*position.asRect.asArray.drop(2))
	}

	//--private

	prSetup {
		ctrl= #[47, 45, 48, 49, 46, 44].collect{|cc, i|   //left hand side
			this.prSetupCC(ctrlSymbols[i], \set, cc);
		};
		knobs= #[14, 15, 16, 17, 18, 19, 20, 21, 22].collect{|cc, i|
			this.prSetupCC(("knob"++(i+1)).asSymbol, \softSet, cc);
		};
		sliders= #[2, 3, 4, 5, 6, 8, 9, 12, 13].collect{|cc, i|
			this.prSetupCC(("slider"++(i+1)).asSymbol, \softSet, cc);
		};
		buttons1= #[23, 24, 25, 26, 27, 28, 29, 30, 31].collect{|cc, i|  //top row
			this.prSetupCC(("button"++(i+1)++"_1").asSymbol, \set, cc);
		};
		buttons2= #[33, 34, 35, 36, 37, 38, 39, 40, 41].collect{|cc, i|  //bottom row
			this.prSetupCC(("button"++(i+1)++"_2").asSymbol, \set, cc);
		};
	}
}
