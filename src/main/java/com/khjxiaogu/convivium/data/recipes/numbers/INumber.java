package com.khjxiaogu.convivium.data.recipes.numbers;

import java.util.function.ToDoubleFunction;

import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.teammoeg.caupona.data.Writeable;

public interface INumber extends Writeable,ToDoubleFunction<IEnvironment>{

}
