/*
 * This file is part of the Money package.
 *
 * Copyright (c) Eriksen Costa <eriksencosta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eriksencosta.money

import com.eriksencosta.math.percentage.Percentage

/**
 * Multiplies a given [Money] by this [Percentage].
 *
 * @receiver[Percentage]
 * @param[money] The [Money] to multiply by this [Percentage].
 * @return The resulting [Money].
 */
public operator fun Percentage.times(money: Money): Money = money * this

/**
 * Increases a given [Money] by this [Percentage].
 *
 * @receiver[Percentage]
 * @param[money] The [Money] value to increase by this [Percentage].
 * @return The resulting [Money].
 */
public infix fun Percentage.increase(money: Money): Money = money increaseBy this

/**
 * Decreases a given [Money] by this [Percentage].
 *
 * @receiver[Percentage]
 * @param[money] The [Money] value to decrease by this [Percentage].
 * @return The resulting [Money].
 */
public infix fun Percentage.decrease(money: Money): Money = money decreaseBy this
