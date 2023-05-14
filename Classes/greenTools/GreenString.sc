GreenString {

	*match {|str, test|  //supports wildcards (*)
		^if(test.isString, {[test]}, {test}).any{|a|
			if(a.indexOf($*).notNil, {
				a.replace("*", ".*").addFirst($^).add($$).matchRegexp(str)
			}, {
				a==str
			})
		}
	}
}
