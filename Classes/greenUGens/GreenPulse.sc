//pulse-train from Miller Puckette Music m170-function-library-v10

GreenPulse {
	*ar {|freq= 440, bw= 100, hp= 9|
		var f= freq*0.5;
		var z= SinOsc.ar(f, 0.5pi, bw/f.max(1));
		^HPF.ar(Sanitize.ar((z*z).neg.exp), hp);
	}
	*kr {|freq= 440, bw= 100, hp= 9|
		var f= freq*0.5;
		var z= SinOsc.kr(f, 0.5pi, bw/f.max(1));
		^HPF.kr(Sanitize.kr((z*z).neg.exp), hp);
	}
}
