/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.model;

import java.util.List;

/**
 *
 * @author shnizle
 */
public abstract class RestModelQueryResponse<T> {
    
    public abstract void done(List<T> models);
    public abstract void failed();
}
