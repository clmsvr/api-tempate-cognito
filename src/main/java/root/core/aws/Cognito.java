package root.core.aws;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import root.auth.util.SecurityProperties;
import root.domain.exception.BusinessException;
import root.domain.model.UsuarioOidc;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChangePasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CodeDeliveryFailureException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InternalErrorException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidLambdaResponseException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidSmsRoleAccessPolicyException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidSmsRoleTrustRelationshipException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.PreconditionNotMetException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.TooManyRequestsException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UnexpectedLambdaException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UnsupportedUserStateException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserLambdaValidationException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

@Component
public class Cognito {

	private SecurityProperties            properties;
	private CognitoIdentityProviderClient client;
	
	public Cognito(SecurityProperties securityProperties) {
		
		properties = securityProperties;
		client = CognitoIdentityProviderClient.builder()
                //.region(Region.US_EAST_1)
				//https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
				// The default credential provider chain, implemented by the DefaultCredentialsProvider class, checks sequentially each of places where you can set default credentials and selects the ﬁrst one you set.
                //.credentialsProvider(ProfileCredentialsProvider.create()) 
                .build();		
	}
	
	public void close() {
		try {
			if (client != null) client.close();
		} catch (Exception e) {
		}
	}
	
	public UsuarioOidc updateUser(String cognitoUserId, String novoNome) 
	throws UserNotFoundException, CognitoIdentityProviderException
	{
    	AttributeType att = AttributeType.builder()
    			.name("name")
    			.value(novoNome)
    			.build();
    	
        AdminUpdateUserAttributesRequest userRequest = AdminUpdateUserAttributesRequest.builder()
                .username(cognitoUserId)
                .userPoolId(properties.getPoolId())
                .userAttributes(att)
                .build();

        client.adminUpdateUserAttributes(userRequest);
        
        return getUser(cognitoUserId);
	}
	
	public List<UsuarioOidc> listUsers()
	throws CognitoIdentityProviderException		
	{
        ListUsersRequest usersRequest = ListUsersRequest.builder()
                .userPoolId(properties.getPoolId())
                .build();

        ListUsersResponse response = client.listUsers(usersRequest);
        
        List<UsuarioOidc> list = new ArrayList<>();
        
        response.users().forEach(user -> {
        	var usuario = UsuarioOidc.builder()
        			.oidcid(user.username())
        			.status(user.userStatusAsString())
        			.dataCadastro(user.userCreateDate().atOffset(ZoneOffset.of("-03:00")))
        			.dataCadastro(user.userLastModifiedDate().atOffset(ZoneOffset.of("-03:00")));
        	
            user.attributes().stream().forEach( att -> {
            	if (att.name().equals("name")) usuario.nome(att.value());
            	if (att.name().equals("email")) usuario.email(att.value());
            });
            
            list.add(usuario.build());
        });
        
        return list;
	}
	
	public UsuarioOidc getUser(String cognitoUserId)
	throws UserNotFoundException, CognitoIdentityProviderException		
	{
        AdminGetUserRequest userRequest = AdminGetUserRequest.builder()
                .username(cognitoUserId)
                .userPoolId(properties.getPoolId())
                .build();

        AdminGetUserResponse response = client.adminGetUser(userRequest);
        
    	var usuario = UsuarioOidc.builder()
    			.oidcid(response.username())
    			.status(response.userStatusAsString())
    			.dataCadastro(response.userCreateDate().atOffset(ZoneOffset.of("-03:00")))
    			.dataAtualizacao(response.userLastModifiedDate().atOffset(ZoneOffset.of("-03:00")));
    	
        response.userAttributes().stream().forEach( att -> {
        	if (att.name().equals("name")) usuario.nome(att.value());
        	if (att.name().equals("email")) usuario.email(att.value());
        });

        return usuario.build();
	}	
	
	public UsuarioOidc createUser(String email, String nome, String senhaTemporaria, Boolean sendEmail)
	throws BusinessException, UserNotFoundException, CognitoIdentityProviderException		
	{

			AttributeType userAttrs = AttributeType.builder()
			        .name("name")
			        .value(nome)
			        .build();

			var builder = AdminCreateUserRequest.builder()
			        .userPoolId(properties.getPoolId())
			        .username(email)
			        .userAttributes(userAttrs);
			
			//se for fornecida uma senha, será permitido suprimir o envio doemail.
			//se a senha nao for fornecida o Cognito gerará a senha, e ela precisará ser informada por email.
			if (senhaTemporaria != null && senhaTemporaria.trim().equals("") == false) {
				builder.temporaryPassword(senhaTemporaria);
				if(sendEmail != null && !sendEmail) 
					builder.messageAction("SUPPRESS"); //nao envia email de senha temp.
			}
			AdminCreateUserRequest request = builder.build();
			AdminCreateUserResponse response = null ;
			
		    try 
		    {	
				response = client.adminCreateUser(request);
			} 
		    catch (CodeDeliveryFailureException e) {
				//usuario foi criado
		    	try {removeUser(email);}catch (Exception ex) {
					Log.error("falha removendo usuario criado com erro :" +ex.getMessage());
				}
				Log.warn("Email do usuario nao aceito: "+ email + " : "+ e.getMessage());
				//remover o usuario porque foi criado
				throw new BusinessException("Email ["+email+"] não foi aceito pelo serviço de email.");
			} 
        
			var user = response.user();
			var usuario = UsuarioOidc.builder()
					.oidcid(user.username())
					.status(user.userStatusAsString())
					.dataCadastro(user.userCreateDate().atOffset(ZoneOffset.of("-03:00")))
					.dataCadastro(user.userLastModifiedDate().atOffset(ZoneOffset.of("-03:00")));
			
			user.attributes().stream().forEach( att -> {
				if (att.name().equals("name")) usuario.nome(att.value());
				if (att.name().equals("email")) usuario.email(att.value());
			});

			return usuario.build();
	}
	
	public UsuarioOidc resetUser(String cognitoUserId, String senhaTemporaria)
	throws UserNotFoundException, CognitoIdentityProviderException		
	{
        var builder = AdminCreateUserRequest.builder()
                .userPoolId(properties.getPoolId())
                .username(cognitoUserId)
                .messageAction("RESEND");
        
        //se a senha nao for fornecida o Cognito gerará a senha, e ela precisará ser informada por email.
        if (senhaTemporaria != null && senhaTemporaria.trim().equals("") == false) {
        	builder.temporaryPassword(senhaTemporaria);
        }
        AdminCreateUserRequest request = builder.build();
        AdminCreateUserResponse response = client.adminCreateUser(request);

        var user = response.user();
    	var usuario = UsuarioOidc.builder()
    			.oidcid(user.username())
    			.status(user.userStatusAsString())
    			.dataCadastro(user.userCreateDate().atOffset(ZoneOffset.of("-03:00")))
    			.dataCadastro(user.userLastModifiedDate().atOffset(ZoneOffset.of("-03:00")));
    	
        user.attributes().stream().forEach( att -> {
        	if (att.name().equals("name")) usuario.nome(att.value());
        	if (att.name().equals("email")) usuario.email(att.value());
        });

        return usuario.build();
	}	
	
	public void removeUser(String cognitoUserId)
	throws UserNotFoundException, CognitoIdentityProviderException		
	{
        AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                .userPoolId(properties.getPoolId())
                .username(cognitoUserId)
                .build();

        //AdminDeleteUserResponse response = 
        client.adminDeleteUser(request);
	}	
	
	public void chantePassword(Jwt accessToken, String senhaAntiga, String novaSenha) 
	throws NotAuthorizedException, CognitoIdentityProviderException
	{
    	ChangePasswordRequest userRequest = ChangePasswordRequest.builder()
                .accessToken(accessToken.getTokenValue())
                .previousPassword(senhaAntiga)
                .proposedPassword(novaSenha)
                .build() ; 
        
    	client.changePassword(userRequest);
	}
	
}
