GreenString {

	*match {|str, array|  //supports wildcards (*)
		^array.any{|a|
			if(a.indexOf($*).notNil, {
				a.replace("*", ".*").addFirst($^).add($$).matchRegexp(str)
			}, {
				a==str
			})
		}
	}
}
