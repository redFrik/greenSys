//nanoKONTROL v2 (8 sliders & 8 knobs, scene #1, factory settings)

//TODO is it possible to control leds on this model?

//related: GreenNanoKontrol2GUI, GreenNanoKontrol

GreenNanoKontrol2 : AbstractGreenMIDIController {

	const <ctrlSymbols= #[
		\cycle, \rew, \ff, \stop, \play, \rec, \trkDec, \trkInc, \mrkSet, \mrkDec, \mrkInc
	];

	var <ctrl, <knobs, <sliders, <buttonsS, <buttonsM, <buttonsR;

	*deviceName {^"nanoKONTROL2"}

	stop {^cvs[\stop]}

	gui {|position|
		^GreenNanoKontrol2GUI(nanoKontrol:this).moveTo(*position.asRect.asArray.drop(2))
	}

	//--private

	prSetup {
		ctrl= #[46, 43, 44, 42, 41, 45, 58, 59, 60, 61, 62].collect{|cc, i|   //left hand side
			this.prSetupCC(ctrlSymbols[i], \set, cc);
		};
		knobs= #[16, 17, 18, 19, 20, 21, 22, 23].collect{|cc, i|
			this.prSetupCC(("knob"++(i+1)).asSymbol, \softSet, cc);
		};
		sliders= #[0, 1, 2, 3, 4, 5, 6, 7].collect{|cc, i|
			this.prSetupCC(("slider"++(i+1)).asSymbol, \softSet, cc);
		};
		buttonsS= #[32, 33, 34, 35, 36, 37, 38, 39].collect{|cc, i|  //top
			this.prSetupCC(("button"++(i+1)++"_S").asSymbol, \set, cc);
		};
		buttonsM= #[48, 49, 50, 51, 52, 53, 54, 55].collect{|cc, i|  //middle
			this.prSetupCC(("button"++(i+1)++"_M").asSymbol, \set, cc);
		};
		buttonsR= #[64, 65, 66, 67, 68, 69, 70, 71].collect{|cc, i|  //bottom
			this.prSetupCC(("button"++(i+1)++"_R").asSymbol, \set, cc);
		};
	}
}
