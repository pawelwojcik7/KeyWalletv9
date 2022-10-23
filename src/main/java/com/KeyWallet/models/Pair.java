package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Pair<T, C> {

    private T left;
    private C right;

}
