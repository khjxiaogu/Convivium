package com.khjxiaogu.convivium.data.recipes.nnumbers;

import java.util.function.Function;

import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.teammoeg.caupona.data.Writeable;

public interface INumber extends Writeable,Function<IEnvironment,Double>{

}
