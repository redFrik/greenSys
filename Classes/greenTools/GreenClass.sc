GreenClass {

	*code {|classOrMethod|
		var ignoreBeginChars= #[$}, $ , $\t, $\r, $\n];
		var str= "";
		File.use(classOrMethod.filenameSymbol.asString, "r", {|f|
			var cnt, chr;
			f.seek(classOrMethod.charPos);
			while {f.pos<f.length and:{cnt.isNil or:{cnt>0}}} {
				chr= f.next;
				if(not(cnt.isNil and:{ignoreBeginChars.includes(chr)}), {
					str= str++chr;
					if(chr==${, {
						cnt= (cnt?0)+1
					}, {
						if(chr==$}, {
							cnt= cnt-1
						})
					})
				})
			}
		});
		^str
	}

	*method {|class, symbol|
		class.methods.do{|m| if(m.name==symbol, {^m})};
		^nil
	}

	//--Post only

	*checkUnusedArguments {|classOrMethod|
		if(classOrMethod.isKindOf(Class), {
			classOrMethod.methods;
		}, {
			[classOrMethod]
		}).do{|m|
			this.prCheckUnusedArguments(m)
		}
	}

	*prCheckUnusedArguments {|method|
		var code;
		if(method.argNames.notNil and:{method.primitiveName.isNil}, {
			code= this.code(method);
			if(code.contains(".subclassResponsibility").not and:{
				code.contains("this.deprecated").not}, {
				method.argNames.do{|v|
					if(v!=\this and:{code.findAll(v.asString).size==1}, {
						"%\t\t\t'%' is probably an unused argument".format(method, v).warn
					})
				}
			})
		})
	}

	*checkUnusedVariables {|classOrMethod|
		if(classOrMethod.isKindOf(Class), {
			classOrMethod.methods
		}, {
			[classOrMethod]
		}).do{|m|
			this.prCheckUnusedVariables(m)
		}
	}

	*prCheckUnusedVariables {|method|
		var code;
		if(method.varNames.notNil, {
			code= this.code(method);
			method.varNames.do{|v|
				if(code.findAll(v.asString).size==1, {
					"%\t\t\t'%' is probably an unused variable".format(method, v).warn
				})
			}
		})
	}
}
