GreenRandDur {
	*ar {|lo= 0, hi= 1|
		var w= WhiteNoise.ar.range(lo, hi);
		^Duty.ar(w.abs, 0, w)
	}
	*kr {|lo= 0, hi= 1|
		var w= WhiteNoise.kr.range(lo, hi);
		^Duty.kr(w.abs, 0, w)
	}
}
