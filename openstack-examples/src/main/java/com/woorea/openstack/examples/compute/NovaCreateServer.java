package com.woorea.openstack.examples.compute;


import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.examples.ExamplesConfiguration;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.Images;
import com.woorea.openstack.nova.model.KeyPairs;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.ServerForCreate;

public class NovaCreateServer {

  /**
   * @param args
   */
  public static void main(String[] args) {
		Keystone keystone = new Keystone(
				ExamplesConfiguration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate()
				.withUsernamePassword(ExamplesConfiguration.KEYSTONE_USERNAME,
						ExamplesConfiguration.KEYSTONE_PASSWORD).execute();

		// use the token in the following requests
		keystone.token(access.getToken().getId());

		Nova nova = new Nova(ExamplesConfiguration.NOVA_ENDPOINT.concat("/")
				.concat(access.getToken().getTenant().getId()));

		nova.token(access.getToken().getId());
		nova.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));

		KeyPairs keysPairs = nova.keyPairs().list().execute();
		Images images = nova.images().list(true).execute();
		Flavors flavors = nova.flavors().list(true).execute();

		ServerForCreate serverForCreate = new ServerForCreate();
		serverForCreate.setName("mooc-" + System.currentTimeMillis());
		serverForCreate.setFlavorRef(flavors.getList().get(0).getId());
		serverForCreate.setImageRef(images.getList().get(1).getId());
		serverForCreate.setKeyName(keysPairs.getList().get(0).getName());
		serverForCreate.getSecurityGroups().add(
				new ServerForCreate.SecurityGroup("default"));

		Server server = nova.servers().boot(serverForCreate).execute();
		System.out.println(server);

  }

}
