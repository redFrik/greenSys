GreenNoise : UGen {
	*ar {|freq= 500, rq= 3, db= 24, mul= 1, add= 0|
		var src= WhiteNoise.ar(mul, add);
		src= BPeakEQ.ar(src, freq, rq, db, 2/db.dbamp);
		^src
	}
}
