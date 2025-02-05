package org.fisco.bcos.web3.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modify!</strong>
 *
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the <a
 * href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.2.
 */
@SuppressWarnings("rawtypes")
public class MyERC20 extends Contract {
    public static final String BINARY =
            "610160604052348015610010575f80fd5b5060405161257f38038061257f833981810160405281019061003291906103f3565b82806040518060400160405280600181526020017f31000000000000000000000000000000000000000000000000000000000000008152508585816003908161007b9190610688565b50806004908161008b9190610688565b5050506100a260058361015b60201b90919060201c565b61012081815250506100be60068261015b60201b90919060201c565b6101408181525050818051906020012060e08181525050808051906020012061010081815250504660a081815250506100fb6101a860201b60201c565b608081815250503073ffffffffffffffffffffffffffffffffffffffff1660c08173ffffffffffffffffffffffffffffffffffffffff16815250505050508060085f6101000a81548160ff021916908360ff160217905550505050610909565b5f60208351101561017c576101758361020260201b60201c565b90506101a2565b8261018c8361026760201b60201c565b5f01908161019a9190610688565b5060ff5f1b90505b92915050565b5f7f8b73c3c69bb8fe3d512ecc4cf759cc79239f7b179b0ffacaa9a75d522b39400f60e0516101005146306040516020016101e79594939291906107bd565b60405160208183030381529060405280519060200120905090565b5f80829050601f8151111561024e57826040517f305a27a90000000000000000000000000000000000000000000000000000000081526004016102459190610856565b60405180910390fd5b80518161025a906108a3565b5f1c175f1b915050919050565b5f819050919050565b5f604051905090565b5f80fd5b5f80fd5b5f80fd5b5f80fd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b6102cf82610289565b810181811067ffffffffffffffff821117156102ee576102ed610299565b5b80604052505050565b5f610300610270565b905061030c82826102c6565b919050565b5f67ffffffffffffffff82111561032b5761032a610299565b5b61033482610289565b9050602081019050919050565b8281835e5f83830152505050565b5f61036161035c84610311565b6102f7565b90508281526020810184848401111561037d5761037c610285565b5b610388848285610341565b509392505050565b5f82601f8301126103a4576103a3610281565b5b81516103b484826020860161034f565b91505092915050565b5f60ff82169050919050565b6103d2816103bd565b81146103dc575f80fd5b50565b5f815190506103ed816103c9565b92915050565b5f805f6060848603121561040a57610409610279565b5b5f84015167ffffffffffffffff8111156104275761042661027d565b5b61043386828701610390565b935050602084015167ffffffffffffffff8111156104545761045361027d565b5b61046086828701610390565b9250506040610471868287016103df565b9150509250925092565b5f81519050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806104c957607f821691505b6020821081036104dc576104db610485565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f6008830261053e7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610503565b6105488683610503565b95508019841693508086168417925050509392505050565b5f819050919050565b5f819050919050565b5f61058c61058761058284610560565b610569565b610560565b9050919050565b5f819050919050565b6105a583610572565b6105b96105b182610593565b84845461050f565b825550505050565b5f90565b6105cd6105c1565b6105d881848461059c565b505050565b5b818110156105fb576105f05f826105c5565b6001810190506105de565b5050565b601f82111561064057610611816104e2565b61061a846104f4565b81016020851015610629578190505b61063d610635856104f4565b8301826105dd565b50505b505050565b5f82821c905092915050565b5f6106605f1984600802610645565b1980831691505092915050565b5f6106788383610651565b9150826002028217905092915050565b6106918261047b565b67ffffffffffffffff8111156106aa576106a9610299565b5b6106b482546104b2565b6106bf8282856105ff565b5f60209050601f8311600181146106f0575f84156106de578287015190505b6106e8858261066d565b86555061074f565b601f1984166106fe866104e2565b5f5b8281101561072557848901518255600182019150602085019450602081019050610700565b86831015610742578489015161073e601f891682610651565b8355505b6001600288020188555050505b505050505050565b5f819050919050565b61076981610757565b82525050565b61077881610560565b82525050565b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f6107a78261077e565b9050919050565b6107b78161079d565b82525050565b5f60a0820190506107d05f830188610760565b6107dd6020830187610760565b6107ea6040830186610760565b6107f7606083018561076f565b61080460808301846107ae565b9695505050505050565b5f82825260208201905092915050565b5f6108288261047b565b610832818561080e565b9350610842818560208601610341565b61084b81610289565b840191505092915050565b5f6020820190508181035f83015261086e818461081e565b905092915050565b5f81519050919050565b5f819050602082019050919050565b5f61089a8251610757565b80915050919050565b5f6108ad82610876565b826108b784610880565b90506108c28161088f565b92506020821015610902576108fd7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff83602003600802610503565b831692505b5050919050565b60805160a05160c05160e051610100516101205161014051611c2561095a5f395f610ae601525f610aab01525f610fdf01525f610fbe01525f61092a01525f61098001525f6109a90152611c255ff3fe608060405234801561000f575f80fd5b50600436106100e8575f3560e01c806370a082311161008a57806395d89b411161006457806395d89b4114610264578063a9059cbb14610282578063d505accf146102b2578063dd62ed3e146102ce576100e8565b806370a08231146101e05780637ecebe001461021057806384b0196e14610240576100e8565b806323b872dd116100c657806323b872dd14610158578063313ce567146101885780633644e515146101a657806340c10f19146101c4576100e8565b806306fdde03146100ec578063095ea7b31461010a57806318160ddd1461013a575b5f80fd5b6100f46102fe565b6040516101019190611495565b60405180910390f35b610124600480360381019061011f9190611546565b61038e565b604051610131919061159e565b60405180910390f35b6101426103b0565b60405161014f91906115c6565b60405180910390f35b610172600480360381019061016d91906115df565b6103b9565b60405161017f919061159e565b60405180910390f35b6101906103e7565b60405161019d919061164a565b60405180910390f35b6101ae6103fc565b6040516101bb919061167b565b60405180910390f35b6101de60048036038101906101d99190611546565b61040a565b005b6101fa60048036038101906101f59190611694565b610418565b60405161020791906115c6565b60405180910390f35b61022a60048036038101906102259190611694565b61045d565b60405161023791906115c6565b60405180910390f35b61024861046e565b60405161025b97969594939291906117bf565b60405180910390f35b61026c610513565b6040516102799190611495565b60405180910390f35b61029c60048036038101906102979190611546565b6105a3565b6040516102a9919061159e565b60405180910390f35b6102cc60048036038101906102c79190611895565b6105c5565b005b6102e860048036038101906102e39190611932565b61070a565b6040516102f591906115c6565b60405180910390f35b60606003805461030d9061199d565b80601f01602080910402602001604051908101604052809291908181526020018280546103399061199d565b80156103845780601f1061035b57610100808354040283529160200191610384565b820191905f5260205f20905b81548152906001019060200180831161036757829003601f168201915b5050505050905090565b5f8061039861078c565b90506103a5818585610793565b600191505092915050565b5f600254905090565b5f806103c361078c565b90506103d08582856107a5565b6103db858585610837565b60019150509392505050565b5f60085f9054906101000a900460ff16905090565b5f610405610927565b905090565b61041482826109dd565b5050565b5f805f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050919050565b5f61046782610a5c565b9050919050565b5f6060805f805f606061047f610aa2565b610487610add565b46305f801b5f67ffffffffffffffff8111156104a6576104a56119cd565b5b6040519080825280602002602001820160405280156104d45781602001602082028036833780820191505090505b507f0f00000000000000000000000000000000000000000000000000000000000000959493929190965096509650965096509650965090919293949596565b6060600480546105229061199d565b80601f016020809104026020016040519081016040528092919081815260200182805461054e9061199d565b80156105995780601f1061057057610100808354040283529160200191610599565b820191905f5260205f20905b81548152906001019060200180831161057c57829003601f168201915b5050505050905090565b5f806105ad61078c565b90506105ba818585610837565b600191505092915050565b8342111561060a57836040517f6279130200000000000000000000000000000000000000000000000000000000815260040161060191906115c6565b60405180910390fd5b5f7f6e71edae12b1b97f4d1f60370fef10105fa2faae0126114a169c64845d6126c98888886106388c610b18565b8960405160200161064e969594939291906119fa565b6040516020818303038152906040528051906020012090505f61067082610b6b565b90505f61067f82878787610b84565b90508973ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146106f357808a6040517f4b800e460000000000000000000000000000000000000000000000000000000081526004016106ea929190611a59565b60405180910390fd5b6106fe8a8a8a610793565b50505050505050505050565b5f60015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2054905092915050565b5f33905090565b6107a08383836001610bb2565b505050565b5f6107b0848461070a565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff81146108315781811015610822578281836040517ffb8f41b200000000000000000000000000000000000000000000000000000000815260040161081993929190611a80565b60405180910390fd5b61083084848484035f610bb2565b5b50505050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036108a7575f6040517f96c6fd1e00000000000000000000000000000000000000000000000000000000815260040161089e9190611ab5565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610917575f6040517fec442f0500000000000000000000000000000000000000000000000000000000815260040161090e9190611ab5565b60405180910390fd5b610922838383610d81565b505050565b5f7f000000000000000000000000000000000000000000000000000000000000000073ffffffffffffffffffffffffffffffffffffffff163073ffffffffffffffffffffffffffffffffffffffff161480156109a257507f000000000000000000000000000000000000000000000000000000000000000046145b156109cf577f000000000000000000000000000000000000000000000000000000000000000090506109da565b6109d7610f9a565b90505b90565b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610a4d575f6040517fec442f05000000000000000000000000000000000000000000000000000000008152600401610a449190611ab5565b60405180910390fd5b610a585f8383610d81565b5050565b5f60075f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050919050565b6060610ad860057f000000000000000000000000000000000000000000000000000000000000000061102f90919063ffffffff16565b905090565b6060610b1360067f000000000000000000000000000000000000000000000000000000000000000061102f90919063ffffffff16565b905090565b5f60075f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f815480929190600101919050559050919050565b5f610b7d610b77610927565b836110dc565b9050919050565b5f805f80610b948888888861111c565b925092509250610ba48282611203565b829350505050949350505050565b5f73ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1603610c22575f6040517fe602df05000000000000000000000000000000000000000000000000000000008152600401610c199190611ab5565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610c92575f6040517f94280d62000000000000000000000000000000000000000000000000000000008152600401610c899190611ab5565b60405180910390fd5b8160015f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20819055508015610d7b578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92584604051610d7291906115c6565b60405180910390a35b50505050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610dd1578060025f828254610dc59190611afb565b92505081905550610e9f565b5f805f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2054905081811015610e5a578381836040517fe450d38c000000000000000000000000000000000000000000000000000000008152600401610e5193929190611a80565b60405180910390fd5b8181035f808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2081905550505b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610ee6578060025f8282540392505081905550610f30565b805f808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051610f8d91906115c6565b60405180910390a3505050565b5f7f8b73c3c69bb8fe3d512ecc4cf759cc79239f7b179b0ffacaa9a75d522b39400f7f00000000000000000000000000000000000000000000000000000000000000007f00000000000000000000000000000000000000000000000000000000000000004630604051602001611014959493929190611b2e565b60405160208183030381529060405280519060200120905090565b606060ff5f1b831461104b5761104483611365565b90506110d6565b8180546110579061199d565b80601f01602080910402602001604051908101604052809291908181526020018280546110839061199d565b80156110ce5780601f106110a5576101008083540402835291602001916110ce565b820191905f5260205f20905b8154815290600101906020018083116110b157829003601f168201915b505050505090505b92915050565b5f6040517f190100000000000000000000000000000000000000000000000000000000000081528360028201528260228201526042812091505092915050565b5f805f7f7fffffffffffffffffffffffffffffff5d576e7357a4501ddfe92f46681b20a0845f1c1115611158575f6003859250925092506111f9565b5f6001888888886040515f815260200160405260405161117b9493929190611b7f565b6020604051602081039080840390855afa15801561119b573d5f803e3d5ffd5b5050506020604051035190505f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16036111ec575f60015f801b935093509350506111f9565b805f805f1b935093509350505b9450945094915050565b5f600381111561121657611215611bc2565b5b82600381111561122957611228611bc2565b5b0315611361576001600381111561124357611242611bc2565b5b82600381111561125657611255611bc2565b5b0361128d576040517ff645eedf00000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b600260038111156112a1576112a0611bc2565b5b8260038111156112b4576112b3611bc2565b5b036112f857805f1c6040517ffce698f70000000000000000000000000000000000000000000000000000000081526004016112ef91906115c6565b60405180910390fd5b60038081111561130b5761130a611bc2565b5b82600381111561131e5761131d611bc2565b5b0361136057806040517fd78bce0c000000000000000000000000000000000000000000000000000000008152600401611357919061167b565b60405180910390fd5b5b5050565b60605f611371836113d7565b90505f602067ffffffffffffffff81111561138f5761138e6119cd565b5b6040519080825280601f01601f1916602001820160405280156113c15781602001600182028036833780820191505090505b5090508181528360208201528092505050919050565b5f8060ff835f1c169050601f81111561141c576040517fb3512b0c00000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b80915050919050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f61146782611425565b611471818561142f565b935061148181856020860161143f565b61148a8161144d565b840191505092915050565b5f6020820190508181035f8301526114ad818461145d565b905092915050565b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f6114e2826114b9565b9050919050565b6114f2816114d8565b81146114fc575f80fd5b50565b5f8135905061150d816114e9565b92915050565b5f819050919050565b61152581611513565b811461152f575f80fd5b50565b5f813590506115408161151c565b92915050565b5f806040838503121561155c5761155b6114b5565b5b5f611569858286016114ff565b925050602061157a85828601611532565b9150509250929050565b5f8115159050919050565b61159881611584565b82525050565b5f6020820190506115b15f83018461158f565b92915050565b6115c081611513565b82525050565b5f6020820190506115d95f8301846115b7565b92915050565b5f805f606084860312156115f6576115f56114b5565b5b5f611603868287016114ff565b9350506020611614868287016114ff565b925050604061162586828701611532565b9150509250925092565b5f60ff82169050919050565b6116448161162f565b82525050565b5f60208201905061165d5f83018461163b565b92915050565b5f819050919050565b61167581611663565b82525050565b5f60208201905061168e5f83018461166c565b92915050565b5f602082840312156116a9576116a86114b5565b5b5f6116b6848285016114ff565b91505092915050565b5f7fff0000000000000000000000000000000000000000000000000000000000000082169050919050565b6116f3816116bf565b82525050565b611702816114d8565b82525050565b5f81519050919050565b5f82825260208201905092915050565b5f819050602082019050919050565b61173a81611513565b82525050565b5f61174b8383611731565b60208301905092915050565b5f602082019050919050565b5f61176d82611708565b6117778185611712565b935061178283611722565b805f5b838110156117b25781516117998882611740565b97506117a483611757565b925050600181019050611785565b5085935050505092915050565b5f60e0820190506117d25f83018a6116ea565b81810360208301526117e4818961145d565b905081810360408301526117f8818861145d565b905061180760608301876115b7565b61181460808301866116f9565b61182160a083018561166c565b81810360c08301526118338184611763565b905098975050505050505050565b61184a8161162f565b8114611854575f80fd5b50565b5f8135905061186581611841565b92915050565b61187481611663565b811461187e575f80fd5b50565b5f8135905061188f8161186b565b92915050565b5f805f805f805f60e0888a0312156118b0576118af6114b5565b5b5f6118bd8a828b016114ff565b97505060206118ce8a828b016114ff565b96505060406118df8a828b01611532565b95505060606118f08a828b01611532565b94505060806119018a828b01611857565b93505060a06119128a828b01611881565b92505060c06119238a828b01611881565b91505092959891949750929550565b5f8060408385031215611948576119476114b5565b5b5f611955858286016114ff565b9250506020611966858286016114ff565b9150509250929050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806119b457607f821691505b6020821081036119c7576119c6611970565b5b50919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b5f60c082019050611a0d5f83018961166c565b611a1a60208301886116f9565b611a2760408301876116f9565b611a3460608301866115b7565b611a4160808301856115b7565b611a4e60a08301846115b7565b979650505050505050565b5f604082019050611a6c5f8301856116f9565b611a7960208301846116f9565b9392505050565b5f606082019050611a935f8301866116f9565b611aa060208301856115b7565b611aad60408301846115b7565b949350505050565b5f602082019050611ac85f8301846116f9565b92915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f611b0582611513565b9150611b1083611513565b9250828201905080821115611b2857611b27611ace565b5b92915050565b5f60a082019050611b415f83018861166c565b611b4e602083018761166c565b611b5b604083018661166c565b611b6860608301856115b7565b611b7560808301846116f9565b9695505050505050565b5f608082019050611b925f83018761166c565b611b9f602083018661163b565b611bac604083018561166c565b611bb9606083018461166c565b95945050505050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602160045260245ffdfea26469706673582212202caa24a385697615b4646e4935eff4a8ec69ed3c39e728f69b5b5523238761ad64736f6c634300081a0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_DOMAIN_SEPARATOR = "DOMAIN_SEPARATOR";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_EIP712DOMAIN = "eip712Domain";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_NONCES = "nonces";

    public static final String FUNC_PERMIT = "permit";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT =
            new Event(
                    "Approval",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>() {}));;

    public static final Event EIP712DOMAINCHANGED_EVENT =
            new Event("EIP712DomainChanged", Arrays.<TypeReference<?>>asList());;

    public static final Event TRANSFER_EVENT =
            new Event(
                    "Transfer",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>() {}));;

    @Deprecated
    protected MyERC20(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MyERC20(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MyERC20(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MyERC20(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses =
                new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<EIP712DomainChangedEventResponse> getEIP712DomainChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, transactionReceipt);
        ArrayList<EIP712DomainChangedEventResponse> responses =
                new ArrayList<EIP712DomainChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EIP712DomainChangedEventResponse getEIP712DomainChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, log);
        EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
        typedResponse.log = log;
        return typedResponse;
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEIP712DomainChangedEventFromLog(log));
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EIP712DOMAINCHANGED_EVENT));
        return eIP712DomainChangedEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses =
                new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> DOMAIN_SEPARATOR() {
        final Function function =
                new Function(
                        FUNC_DOMAIN_SEPARATOR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final Function function =
                new Function(
                        FUNC_ALLOWANCE,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, owner),
                                new org.web3j.abi.datatypes.Address(160, spender)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final Function function =
                new Function(
                        FUNC_APPROVE,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, spender),
                                new org.web3j.abi.datatypes.generated.Uint256(value)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final Function function =
                new Function(
                        FUNC_BALANCEOF,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final Function function =
                new Function(
                        FUNC_DECIMALS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<
                    Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>>
            eip712Domain() {
        final Function function =
                new Function(
                        FUNC_EIP712DOMAIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes1>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes32>() {},
                                new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<
                Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>>(
                function,
                new Callable<
                        Tuple7<
                                byte[],
                                String,
                                String,
                                BigInteger,
                                String,
                                byte[],
                                List<BigInteger>>>() {
                    @Override
                    public Tuple7<
                                    byte[],
                                    String,
                                    String,
                                    BigInteger,
                                    String,
                                    byte[],
                                    List<BigInteger>>
                            call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<
                                byte[],
                                String,
                                String,
                                BigInteger,
                                String,
                                byte[],
                                List<BigInteger>>(
                                (byte[]) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (String) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue(),
                                (String) results.get(4).getValue(),
                                (byte[]) results.get(5).getValue(),
                                convertToNative((List<Uint256>) results.get(6).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> mint(String to, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_MINT,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, to),
                                new org.web3j.abi.datatypes.generated.Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final Function function =
                new Function(
                        FUNC_NAME,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> nonces(String owner) {
        final Function function =
                new Function(
                        FUNC_NONCES,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> permit(
            String owner,
            String spender,
            BigInteger value,
            BigInteger deadline,
            BigInteger v,
            byte[] r,
            byte[] s) {
        final Function function =
                new Function(
                        FUNC_PERMIT,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, owner),
                                new org.web3j.abi.datatypes.Address(160, spender),
                                new org.web3j.abi.datatypes.generated.Uint256(value),
                                new org.web3j.abi.datatypes.generated.Uint256(deadline),
                                new org.web3j.abi.datatypes.generated.Uint8(v),
                                new org.web3j.abi.datatypes.generated.Bytes32(r),
                                new org.web3j.abi.datatypes.generated.Bytes32(s)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function =
                new Function(
                        FUNC_SYMBOL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final Function function =
                new Function(
                        FUNC_TOTALSUPPLY,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger value) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, to),
                                new org.web3j.abi.datatypes.generated.Uint256(value)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(
            String from, String to, BigInteger value) {
        final Function function =
                new Function(
                        FUNC_TRANSFERFROM,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Address(160, from),
                                new org.web3j.abi.datatypes.Address(160, to),
                                new org.web3j.abi.datatypes.generated.Uint256(value)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static MyERC20 load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new MyERC20(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MyERC20 load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new MyERC20(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MyERC20 load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new MyERC20(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MyERC20 load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return new MyERC20(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<MyERC20> deploy(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider,
            String name_,
            String symbol_,
            BigInteger decimals_) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Utf8String(name_),
                                new org.web3j.abi.datatypes.Utf8String(symbol_),
                                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(
                MyERC20.class,
                web3j,
                credentials,
                contractGasProvider,
                getDeploymentBinary(),
                encodedConstructor);
    }

    public static RemoteCall<MyERC20> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider,
            String name_,
            String symbol_,
            BigInteger decimals_) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Utf8String(name_),
                                new org.web3j.abi.datatypes.Utf8String(symbol_),
                                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(
                MyERC20.class,
                web3j,
                transactionManager,
                contractGasProvider,
                getDeploymentBinary(),
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MyERC20> deploy(
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String name_,
            String symbol_,
            BigInteger decimals_) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Utf8String(name_),
                                new org.web3j.abi.datatypes.Utf8String(symbol_),
                                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(
                MyERC20.class,
                web3j,
                credentials,
                gasPrice,
                gasLimit,
                getDeploymentBinary(),
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MyERC20> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String name_,
            String symbol_,
            BigInteger decimals_) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.Utf8String(name_),
                                new org.web3j.abi.datatypes.Utf8String(symbol_),
                                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(
                MyERC20.class,
                web3j,
                transactionManager,
                gasPrice,
                gasLimit,
                getDeploymentBinary(),
                encodedConstructor);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class EIP712DomainChangedEventResponse extends BaseEventResponse {}

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
