/*
 * Copyright 2014 Fluo authors (see AUTHORS)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fluo.accumulo.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.Filter;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;

public class NotificationHashFilter extends Filter {

  private static final String DIVISOR_OPT = "divisor";
  private static final String REMAINDER_OPT = "remainder";
  
  private int divisor;
  private int remainder;

  @Override
  public boolean accept(Key k, Value v) {
    return Math.abs(k.getRowData().hashCode() + k.getColumnQualifierData().hashCode()) % divisor == remainder;
  }
  
  @Override
  public void init(SortedKeyValueIterator<Key,Value> source, Map<String, String> options, IteratorEnvironment env) throws IOException{
    super.init(source, options, env);
    
    this.divisor = Integer.parseInt(options.get(DIVISOR_OPT));
    this.remainder = Integer.parseInt(options.get(REMAINDER_OPT));
  }
  
  public static void setModulusParams(IteratorSetting iterCfg, int divisor, int remainder){
    if(remainder < 0)
      throw new IllegalArgumentException("remainder < 0 : "+remainder);
    
    if(divisor <= 0)
      throw new IllegalArgumentException("divisor <= 0 : "+divisor);
    
    if(remainder >= divisor)
      throw new IllegalArgumentException("remainder >= divisor : "+remainder+","+divisor);
    
    iterCfg.addOption(DIVISOR_OPT, divisor+"");
    iterCfg.addOption(REMAINDER_OPT, remainder+"");
  }
}
