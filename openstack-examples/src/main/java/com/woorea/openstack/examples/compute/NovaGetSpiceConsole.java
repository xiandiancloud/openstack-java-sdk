package com.woorea.openstack.examples.compute;


import com.woorea.openstack.examples.ExamplesConfiguration;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.api.ServersResource.GetSpiceConsoleServer;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.ServerAction.SpiceConsole;
import com.woorea.openstack.nova.model.Servers;

public class NovaGetSpiceConsole {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(ExamplesConfiguration.KEYSTONE_AUTH_URL);
		Access access = keystone.tokens().authenticate(new UsernamePassword(ExamplesConfiguration.KEYSTONE_USERNAME, ExamplesConfiguration.KEYSTONE_PASSWORD))
				.withTenantName("admin")
				.execute();
		
		//use the token in the following requests
		keystone.token(access.getToken().getId());
			
		//NovaClient novaClient = new NovaClient(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"), access.getToken().getId());
		Nova novaClient = new Nova(ExamplesConfiguration.NOVA_ENDPOINT.concat("/").concat(access.getToken().getTenant().getId()));
		novaClient.token(access.getToken().getId());
		//novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024);
		
		Servers servers = novaClient.servers().list(true).execute();
		String id = null;
		for(Server server : servers) {
			id = server.getId();
			System.out.println(server);
		}
		//use server id to get its spice console
		if (id != null)
		{
			GetSpiceConsoleServer cs = novaClient.servers().getSpiceConsole(id, "spice-html5");//novnc"spice-html5"
			SpiceConsole console = cs.execute();		
			
			System.out.println(console.getUrl());		
			
		}
		
	}

}
