import React from "react";
import axios from "axios";

const backendBaseUrl = 'http://localhost:8080';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {}
    this.logout = this.logout.bind(this);
  }

  componentDidMount() {
    let jwt;
    if (document.cookie.indexOf('jwt-token=') !== -1) {
      jwt = document.cookie
          .split('; ')
          .find(row => row.startsWith('jwt-token='))
          .split('=')[1];
    } else {
      jwt = '';
    }

    axios({
      method: 'get',
      url: backendBaseUrl + '/current-user',
      withCredentials: true,
      headers: {
        'Authorization': 'Bearer ' + jwt
      }
    }).then(response => {
      this.setState({
        user: response.data
      })
    }).catch(error => {
      console.log('GETTING USER FAILED', error)
      alert('Getting current user failed, check console for details')
    })
  }

  render() {
    if (this.state.user) {
      console.log(this.state.user);
      let name = this.state.user.fullName;
      if (!name) {
        name = this.state.user.name;
      }
      return (
          <div>
            <p>Current authenticated user: {name}</p>
            <p>Whole authentication object is logged in console for reference.</p>
            <input type="button" value="Log out" onClick={this.logout}/>
          </div>
      )
    } else {
      return (
          <div>
            <p>Not logged in.</p>
            <input type="button" value="Log in with Google" onClick={() => window.location = backendBaseUrl + '/login'}/>
          </div>
      )
    }
  }

  logout() {
    axios({
      method: 'post',
      url: backendBaseUrl + '/logout',
      withCredentials: true
    }).then(() => {
      window.location = '/'
    }).catch(error => {
      console.log('LOG OUT FAILED', error)
      alert('Log out failed, check console for details')
    });
  }
}

export default App;
