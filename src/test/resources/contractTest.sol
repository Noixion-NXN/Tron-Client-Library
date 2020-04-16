pragma solidity ^0.4.23;

contract Test{

    string stringParam;
    uint256 uintParam;

    constructor (string _stringParam, uint256 _uintParamm) public{
        stringParam = _stringParam;
        uintParam = _uintParamm;
    }

    function f() public pure returns (string){
        return "method f()";
    }

    function g() public pure returns (string){
        return "method g()";
    }
}